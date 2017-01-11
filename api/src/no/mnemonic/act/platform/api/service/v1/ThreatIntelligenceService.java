package no.mnemonic.act.platform.api.service.v1;

import no.mnemonic.act.platform.api.exceptions.AccessDeniedException;
import no.mnemonic.act.platform.api.exceptions.AuthenticationFailedException;
import no.mnemonic.act.platform.api.exceptions.InvalidArgumentException;
import no.mnemonic.act.platform.api.exceptions.ObjectNotFoundException;
import no.mnemonic.act.platform.api.model.v1.*;
import no.mnemonic.act.platform.api.model.v1.Object;
import no.mnemonic.act.platform.api.request.v1.*;

/**
 * Service handling all Threat Intelligence data - i.e. Facts, Objects and related data - stored inside the ACT platform.
 */
public interface ThreatIntelligenceService {

  /**
   * Fetch an ObjectType by its id.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying ObjectType.
   * @return ObjectType identified by its id.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the requested ObjectType could not be found.
   */
  default ObjectType getObjectType(RequestHeader rh, GetObjectTypeByIdRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Search existing ObjectTypes.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request limiting the returned ObjectTypes.
   * @return All ObjectTypes fulfilling the request parameters.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default ResultSet<ObjectType> searchObjectTypes(RequestHeader rh, SearchObjectTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new ObjectType.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing new ObjectType.
   * @return Newly created ObjectType.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default ObjectType createObjectType(RequestHeader rh, CreateObjectTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Update an existing ObjectType.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing updates to an existing ObjectType.
   * @return Updated ObjectType.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the ObjectType could not be found.
   */
  default ObjectType updateObjectType(RequestHeader rh, UpdateObjectTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch an Object by its id.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying Object.
   * @return Object identified by its id.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the requested Object could not be found.
   */
  default Object getObject(RequestHeader rh, GetObjectByIdRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch an Object by its type and value.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying Object.
   * @return Object identified by its type and value.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the requested Object could not be found.
   */
  default Object getObject(RequestHeader rh, GetObjectByTypeValueRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Search existing Objects.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request limiting the returned Objects.
   * @return All Objects fulfilling the request parameters.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default ResultSet<Object> searchObjects(RequestHeader rh, SearchObjectRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch Facts bound to an Object.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request limiting the returned Facts.
   * @return Facts bound to a specific Object.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Object to fetch Facts for could not be found.
   */
  default ResultSet<Fact> searchObjectFacts(RequestHeader rh, SearchObjectFactsRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch a FactType by its id.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying FactType.
   * @return FactType identified by its id.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the requested FactType could not be found.
   */
  default FactType getFactType(RequestHeader rh, GetFactTypeByIdRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Search existing FactTypes.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request limiting the returned FactTypes.
   * @return All FactTypes fulfilling the request parameters.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default ResultSet<FactType> searchFactTypes(RequestHeader rh, SearchFactTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new FactType.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing new FactType.
   * @return Newly created FactType.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default FactType createFactType(RequestHeader rh, CreateFactTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Update an existing FactType.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing updates to an existing FactType.
   * @return Updated FactType.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the FactType could not be found.
   */
  default FactType updateFactType(RequestHeader rh, UpdateFactTypeRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch a Fact by its id.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying Fact.
   * @return Fact identified by its id.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact could not be found.
   */
  default Fact getFact(RequestHeader rh, GetFactByIdRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Create a new Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing new Fact.
   * @return Newly created Fact.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   */
  default Fact createFact(RequestHeader rh, CreateFactRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException {
    throw new UnsupportedOperationException();
  }

  /**
   * Retract an existing Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing retraction Fact.
   * @return Newly created retraction Fact.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact to retract could not be found.
   */
  default Fact retractFact(RequestHeader rh, RetractFactRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch access control list of a Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying Fact.
   * @return Access control list of a Fact.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact to fetch ACL for could not be found.
   */
  default ResultSet<AclEntry> getFactAcl(RequestHeader rh, GetFactAclRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Grant a Subject access to a Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing the grant request.
   * @return Newly created access control list entry.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact to grant access to could not be found.
   */
  default AclEntry grantFactAccess(RequestHeader rh, GrantFactAccessRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Fetch comments of a Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request identifying Fact and limiting returned comments.
   * @return Comments of a Fact.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact to fetch comments for could not be found.
   */
  default ResultSet<FactComment> getFactComments(RequestHeader rh, GetFactCommentsRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

  /**
   * Add a comment to a Fact.
   *
   * @param rh      Contains meta data about the request.
   * @param request Request containing comment.
   * @return Newly created comment.
   * @throws AccessDeniedException         If the user is not allowed to perform this operation.
   * @throws AuthenticationFailedException If the user could not be authenticated.
   * @throws InvalidArgumentException      If the request contains invalid parameters.
   * @throws ObjectNotFoundException       If the Fact to add comment to could not be found.
   */
  default FactComment createFactComment(RequestHeader rh, CreateFactCommentRequest request)
          throws AccessDeniedException, AuthenticationFailedException, InvalidArgumentException, ObjectNotFoundException {
    throw new UnsupportedOperationException();
  }

}