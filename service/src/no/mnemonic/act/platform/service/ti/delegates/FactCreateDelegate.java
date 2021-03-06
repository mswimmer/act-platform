package no.mnemonic.act.platform.service.ti.delegates;

import com.google.common.collect.Streams;
import no.mnemonic.act.platform.api.exceptions.AccessDeniedException;
import no.mnemonic.act.platform.api.exceptions.AuthenticationFailedException;
import no.mnemonic.act.platform.api.exceptions.InvalidArgumentException;
import no.mnemonic.act.platform.api.model.v1.Fact;
import no.mnemonic.act.platform.api.model.v1.Organization;
import no.mnemonic.act.platform.api.request.v1.CreateFactRequest;
import no.mnemonic.act.platform.dao.api.FactExistenceSearchCriteria;
import no.mnemonic.act.platform.dao.cassandra.entity.*;
import no.mnemonic.act.platform.dao.elastic.document.FactDocument;
import no.mnemonic.act.platform.dao.elastic.document.SearchResult;
import no.mnemonic.act.platform.service.contexts.TriggerContext;
import no.mnemonic.act.platform.service.ti.TiFunctionConstants;
import no.mnemonic.act.platform.service.ti.TiRequestContext;
import no.mnemonic.act.platform.service.ti.TiSecurityContext;
import no.mnemonic.act.platform.service.ti.TiServiceEvent;
import no.mnemonic.act.platform.service.ti.helpers.FactStorageHelper;
import no.mnemonic.act.platform.service.ti.helpers.FactTypeResolver;
import no.mnemonic.act.platform.service.ti.helpers.ObjectResolver;
import no.mnemonic.act.platform.service.validators.Validator;
import no.mnemonic.commons.utilities.ObjectUtils;
import no.mnemonic.commons.utilities.collections.CollectionUtils;
import no.mnemonic.commons.utilities.collections.SetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class FactCreateDelegate extends AbstractDelegate {

  private final FactTypeResolver factTypeResolver;
  private final ObjectResolver objectResolver;
  private final FactStorageHelper factStorageHelper;

  private FactCreateDelegate(FactTypeResolver factTypeResolver, ObjectResolver objectResolver, FactStorageHelper factStorageHelper) {
    this.factTypeResolver = factTypeResolver;
    this.objectResolver = objectResolver;
    this.factStorageHelper = factStorageHelper;
  }

  public Fact handle(CreateFactRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    // Verify that user is allowed to add Facts for the requested organization.
    TiSecurityContext.get().checkPermission(TiFunctionConstants.addFactObjects, resolveOrganization(request.getOrganization()));

    // Validate that requested Fact matches its FactType.
    FactTypeEntity type = factTypeResolver.resolveFactType(request.getType());
    validateFactValue(type, request.getValue());
    validateFactObjectBindings(type, request.getBindings());

    FactEntity fact = resolveExistingFact(request, type);
    if (fact != null) {
      // Refresh an existing Fact.
      fact = TiRequestContext.get().getFactManager().refreshFact(fact.getId());
      List<UUID> subjectsAddedToAcl = factStorageHelper.saveAdditionalAclForFact(fact, request.getAcl());
      // Reindex existing Fact in ElasticSearch.
      reindexExistingFact(fact, subjectsAddedToAcl);
    } else {
      // Or create a new Fact.
      fact = saveFact(request, type);
      List<UUID> subjectsAddedToAcl = factStorageHelper.saveInitialAclForNewFact(fact, request.getAcl());
      // Index new Fact into ElasticSearch.
      indexCreatedFact(fact, type, subjectsAddedToAcl);
    }

    // Always add provided comment.
    factStorageHelper.saveCommentForFact(fact, request.getComment());

    // Register TriggerEvent before returning added Fact.
    Fact addedFact = TiRequestContext.get().getFactConverter().apply(fact);
    registerTriggerEvent(addedFact);

    return addedFact;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private FactTypeResolver factTypeResolver;
    private ObjectResolver objectResolver;
    private FactStorageHelper factStorageHelper;

    private Builder() {
    }

    public FactCreateDelegate build() {
      ObjectUtils.notNull(factTypeResolver, "Cannot instantiate FactCreateDelegate without 'factTypeResolver'.");
      ObjectUtils.notNull(objectResolver, "Cannot instantiate FactCreateDelegate without 'objectResolver'.");
      ObjectUtils.notNull(factStorageHelper, "Cannot instantiate FactCreateDelegate without 'factStorageHelper'.");
      return new FactCreateDelegate(factTypeResolver, objectResolver, factStorageHelper);
    }

    public Builder setFactTypeResolver(FactTypeResolver factTypeResolver) {
      this.factTypeResolver = factTypeResolver;
      return this;
    }

    public Builder setObjectResolver(ObjectResolver objectResolver) {
      this.objectResolver = objectResolver;
      return this;
    }

    public Builder setFactStorageHelper(FactStorageHelper factStorageHelper) {
      this.factStorageHelper = factStorageHelper;
      return this;
    }
  }

  private void validateFactValue(FactTypeEntity type, String value) throws InvalidArgumentException {
    Validator validator = TiRequestContext.get().getValidatorFactory().get(type.getValidator(), type.getValidatorParameter());
    if (!validator.validate(value)) {
      throw new InvalidArgumentException()
              .addValidationError("Fact did not pass validation against FactType.", "fact.not.valid", "value", value);
    }
  }

  private void validateFactObjectBindings(FactTypeEntity type, List<CreateFactRequest.FactObjectBinding> requestedBindings)
          throws InvalidArgumentException {
    InvalidArgumentException ex = new InvalidArgumentException();

    for (int i = 0; i < requestedBindings.size(); i++) {
      CreateFactRequest.FactObjectBinding requested = requestedBindings.get(i);
      ObjectEntity object = objectResolver.resolveObject(requested.getObjectID(), requested.getObjectType(), requested.getObjectValue());

      // Check requested binding against all definitions.
      boolean valid = type.getRelevantObjectBindings()
              .stream()
              .anyMatch(b -> Objects.equals(b.getObjectTypeID(), object.getTypeID()) && Objects.equals(b.getDirection().name(), requested.getDirection().name()));
      if (!valid) {
        // Requested binding is invalid, add to exception and continue to validate other bindings.
        ex.addValidationError("Requested binding between Fact and Object is not allowed.", "invalid.fact.object.binding", "bindings." + i, requested.toString());
      }
    }

    if (!CollectionUtils.isEmpty(ex.getValidationErrors())) {
      throw ex;
    }
  }

  private FactEntity resolveExistingFact(CreateFactRequest request, FactTypeEntity type) throws InvalidArgumentException {
    // Skip confidenceLevel for now as it's currently not provided in the request.
    FactExistenceSearchCriteria.Builder criteriaBuilder = FactExistenceSearchCriteria.builder()
            .setFactValue(request.getValue())
            .setFactTypeID(type.getId())
            .setSourceID(resolveSource(request.getSource()))
            .setOrganizationID(resolveOrganization(request.getOrganization()))
            .setAccessMode(request.getAccessMode().name());
    // Need to resolve bindings in order to get the correct objectID if this isn't provided in the request.
    for (FactEntity.FactObjectBinding binding : resolveFactObjectBindings(request.getBindings())) {
      criteriaBuilder.addObject(binding.getObjectID(), binding.getDirection().name());
    }

    // Try to fetch any existing Facts from ElasticSearch.
    SearchResult<FactDocument> result = TiRequestContext.get().getFactSearchManager().retrieveExistingFacts(criteriaBuilder.build());
    if (result.getCount() <= 0) {
      return null; // No results, need to create new Fact.
    }

    // Fetch the authorative data from Cassandra, apply permission check and return existing Fact if accessible.
    List<UUID> factID = result.getValues().stream().map(FactDocument::getId).collect(Collectors.toList());
    return Streams.stream(TiRequestContext.get().getFactManager().getFacts(factID))
            .filter(fact -> TiSecurityContext.get().hasReadPermission(fact))
            .findFirst()
            .orElse(null);
  }

  private FactEntity saveFact(CreateFactRequest request, FactTypeEntity type)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    FactEntity fact = new FactEntity()
            .setId(UUID.randomUUID())  // Need to provide client-generated ID.
            .setTypeID(type.getId())
            .setValue(request.getValue())
            .setAccessMode(AccessMode.valueOf(request.getAccessMode().name()))
            .setInReferenceToID(resolveInReferenceTo(request.getInReferenceTo()))
            .setOrganizationID(resolveOrganization(request.getOrganization()))
            .setSourceID(resolveSource(request.getSource()))
            .setBindings(resolveFactObjectBindings(request.getBindings()))
            .setTimestamp(System.currentTimeMillis())
            .setLastSeenTimestamp(System.currentTimeMillis());

    fact = TiRequestContext.get().getFactManager().saveFact(fact);
    // Save all bindings between Objects and the created Facts.
    for (FactEntity.FactObjectBinding binding : fact.getBindings()) {
      ObjectFactBindingEntity entity = new ObjectFactBindingEntity()
              .setObjectID(binding.getObjectID())
              .setFactID(fact.getId())
              .setDirection(binding.getDirection());
      TiRequestContext.get().getObjectManager().saveObjectFactBinding(entity);
    }

    return fact;
  }

  private UUID resolveInReferenceTo(UUID inReferenceToID)
          throws InvalidArgumentException, AccessDeniedException, AuthenticationFailedException {
    if (inReferenceToID != null) {
      FactEntity inReferenceTo = TiRequestContext.get().getFactManager().getFact(inReferenceToID);
      if (inReferenceTo == null) {
        // Referenced Fact must exist.
        throw new InvalidArgumentException()
                .addValidationError("Referenced Fact does not exist.", "referenced.fact.not.exist", "inReferenceTo", inReferenceToID.toString());
      }
      // User must have access to referenced Fact.
      TiSecurityContext.get().checkReadPermission(inReferenceTo);
    }
    // Everything ok, just return ID.
    return inReferenceToID;
  }

  private List<FactEntity.FactObjectBinding> resolveFactObjectBindings(List<CreateFactRequest.FactObjectBinding> requestedBindings)
          throws InvalidArgumentException {
    List<FactEntity.FactObjectBinding> entityBindings = new ArrayList<>();

    for (CreateFactRequest.FactObjectBinding requested : requestedBindings) {
      ObjectEntity object = objectResolver.resolveObject(requested.getObjectID(), requested.getObjectType(), requested.getObjectValue());
      FactEntity.FactObjectBinding entity = new FactEntity.FactObjectBinding()
              .setObjectID(object.getId())
              .setDirection(Direction.valueOf(requested.getDirection().name()));
      entityBindings.add(entity);
    }

    return entityBindings;
  }

  private void reindexExistingFact(FactEntity fact, List<UUID> acl) {
    reindexExistingFact(fact.getId(), document -> {
      // Only 'lastSeenTimestamp' and potentially the ACL have been changed when refreshing a Fact.
      document.setLastSeenTimestamp(fact.getLastSeenTimestamp());
      document.setAcl(SetUtils.union(document.getAcl(), SetUtils.set(acl)));
      return document;
    });
  }

  private void registerTriggerEvent(Fact addedFact) {
    TiServiceEvent event = TiServiceEvent.forEvent(TiServiceEvent.EventName.FactAdded)
            .setOrganization(ObjectUtils.ifNotNull(addedFact.getOrganization(), Organization.Info::getId))
            .setAccessMode(addedFact.getAccessMode())
            .addContextParameter(TiServiceEvent.ContextParameter.AddedFact.name(), addedFact)
            .build();
    TriggerContext.get().registerTriggerEvent(event);
  }

}
