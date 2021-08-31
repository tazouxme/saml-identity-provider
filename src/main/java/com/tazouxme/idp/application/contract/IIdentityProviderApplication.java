package com.tazouxme.idp.application.contract;

import java.util.Set;

import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.exception.FederationException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.Role;
import com.tazouxme.idp.model.User;

public interface IIdentityProviderApplication {
	
	/**
	 * Create an Access to an Application (and a Federation if enabled)
	 * @param user - User to give access
	 * @param application - Application to be accessed
	 * @param role - Role when accessing application
	 * @param organization - Organization to which the Access belongs
	 * @param createdBy - User who creates the record
	 * @return The created Access
	 * @throws AccessException
	 */
	public Access createAccess(User user, Application application, Role role, Organization organization, String createdBy) throws AccessException;
	
	/**
	 * Update an Access entry in the database
	 * @param externalId - Access External ID
	 * @param enabled - Is access enabled
	 * @param organization - Organization to which the Access belongs
	 * @return The updated Access
	 * @throws AccessException
	 */
	public Access updateAccess(String externalId, boolean enabled, Organization organization) throws AccessException;
	
	/**
	 * Delete an Access entry in the database
	 * @param externalId - Access External ID
	 * @param organization - Organization to which the Access belongs
	 * @throws AccessException
	 */
	public void deleteAccess(String externalId, Organization organization) throws AccessException;
	
	/**
	 * Get all Activations for a specific User
	 * @param userExternalId - User External ID
	 * @param organizationId - External Organization ID
	 * @return All found Activations
	 */
	public Set<Activation> findActivationsByUser(String userExternalId, String organizationId);
	
	/**
	 * Create an Activation entry in the database
	 * @param organizationId - External Organization ID
	 * @param userId - External User ID
	 * @param step - 'ACTIVATE' or 'PASSWORD'
	 * @param createdBy - User who creates the record
	 * @return The created Activation
	 * @throws ActivationException - When an Activation with the same External ID exists
	 */
	public Activation createActivation(String organizationId, String userId, String step, String createdBy) throws ActivationException;
	
	/**
	 * Get all existing registered Applications for the Organization
	 * @param organizationId - External Organization ID
	 * @return All existing Applications
	 */
	public Set<Application> findAllApplications(String organizationId);
	
	/**
	 * Get the Application by its External ID
	 * @param externalId - Application's External ID
	 * @param organizationId - External Organization ID
	 * @return The Application
	 * @throws ApplicationException - When no Application exists
	 */
	public Application findApplicationByExternalId(String externalId, String organizationId) throws ApplicationException;
	
	/**
	 * Get the Application by its URN
	 * @param urn - Application's URN
	 * @param organizationId - External Organization ID
	 * @return The Application
	 * @throws ApplicationException - When no Application exists
	 */
	public Application findApplicationByURN(String urn, String organizationId) throws ApplicationException;
	
	/**
	 * Create an Application entry in the database
	 * @param urn - Application's URN
	 * @param name - Application's name
	 * @param description - Application's description (may be empty)
	 * @param acsUrl - Application's Assertion Consumer Service URL
	 * @param logoutUrl - Application's Logout URL
	 * @param organization - Organization to which the Application belongs
	 * @param createdBy - User who creates the record
	 * @return The created Application
	 * @throws ApplicationException - When an Application with External ID already exists
	 */
	public Application createApplication(String urn, String name, String description, String acsUrl, String logoutUrl, Organization organization, String createdBy) throws ApplicationException;
	
	/**
	 * Update an Application entry in the database
	 * @param externalId - Application's External Id
	 * @param urn - Application's URN
	 * @param name - Application's name
	 * @param description - Application's description (may be empty)
	 * @param acsUrl - Application's Assertion Consumer Service URL
	 * @param logoutUrl - Application's Logout URL
	 * @param organization - Organization to which the Application belongs
	 * @return The updated Application
	 * @throws ApplicationException - When an Application with External ID does not exist
	 */
	public Application updateApplication(String externalId, String urn, String name, String description, String acsUrl, String logoutUrl, Organization organization) throws ApplicationException;
	
	/**
	 * Update an Application entry in the database
	 * @param externalId - Application's External Id
	 * @param claims - Application's claims
	 * @param organization - Organization to which the Application belongs
	 * @return The updated Application
	 * @throws ApplicationException - When an Application with External ID does not exist
	 */
	public Application updateApplicationClaims(String externalId, Set<Claim> claims, Organization organization) throws ApplicationException;
	
	/**
	 * Delete an Application entry in the database
	 * @param externalId - Application's External Id
	 * @param organization - Organization to which the Application belongs
	 * @throws ApplicationException - When an Application with External ID does not exist
	 */
	public void deleteApplication(String externalId, Organization organization) throws ApplicationException;
	
	/**
	 * Find Federations identity for Application
	 * @param urn - Application's URN
	 * @param organizationExternalId - Organization External IDOrganization External ID
	 * @return All existing Federations for the Application 
	 */
	public Set<Federation> findFederationsByURN(String urn, String organizationExternalId);
	
	/**
	 * Find Federations identity for User
	 * @param userExternalId - User External ID
	 * @param organizationExternalId - Organization External IDOrganization External ID
	 * @return All existing Federations for the User 
	 */
	public Set<Federation> findFederationsByUser(String userExternalId, String organizationExternalId);
	
	/**
	 * Find a Federation identity for User and Application
	 * @param userExternalId - User External ID
	 * @param urn - Application's URN
	 * @param organizationExternalId - Organization External IDOrganization External ID
	 * @return The Federation identity for the User
	 * @throws FederationException - When a Federation does not exist
	 */
	public Federation findFederationByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws FederationException;
	
	/**
	 * Create a new Federation entry for User against an Application
	 * @param user - User to give access
	 * @param application - Application to be accessed
	 * @param organization - Organization to which the Access belongs
	 * @param createdBy - User who creates the record
	 * @return The created Federation
	 * @throws FederationException
	 */
	public Federation createFederation(User user, Application application, Organization organization, String createdBy) throws FederationException;
	
	/**
	 * Get an Organization via its External ID
	 * @param externalId - Organization External ID
	 * @return The Organization
	 * @throws OrganizationException - When no Organization exists
	 */
	public Organization findOrganizationByExternalId(String externalId) throws OrganizationException;
	
	/**
	 * Get an Organization via its domain
	 * @param domain - The Organization's domain
	 * @return The Organization
	 * @throws OrganizationException - When no Organization exists
	 */
	public Organization findOrganizationByDomain(String domain) throws OrganizationException;
	
	/**
	 * Create an Organization entry in the database
	 * @param code - Organization Code
	 * @param domain - Organization domain
	 * @param createdBy - User who creates the record
	 * @return The created Organization
	 * @throws OrganizationException - When the Organization with External ID or domain already exists
	 */
	public Organization createOrganization(String code, String domain, String createdBy) throws OrganizationException;
	
	/**
	 * Update an Organization entry in the database
	 * @param id - Organization External ID
	 * @param name - Organization name
	 * @param description - Organization description (may be null)
	 * @param federation - Federation for User authentication is enabled
	 * @return The updated Organization
	 * @throws OrganizationException - When the Organization with External ID or domain does not exist
	 */
	public Organization updateOrganization(String id, String name, String description, boolean federation) throws OrganizationException;
	
	/**
	 * Update an Organization entry in the database
	 * @param id - Organization External ID
	 * @param certificate - Organization encoded Certificate
	 * @return The updated Organization
	 * @throws OrganizationException - When the Organization with External ID or domain does not exist
	 */
	public Organization setCertificate(String id, String certificate) throws OrganizationException;
	
	/**
	 * Update an Organization entry in the database
	 * @param id - Organization External ID
	 * @return The updated Organization
	 * @throws OrganizationException - When the Organization with External ID or domain does not exist
	 */
	public Organization deleteCertificate(String id) throws OrganizationException;
	
	/**
	 * Get all Claims
	 * @param organizationId - Organization External ID
	 * @return 
	 */
	public Set<Claim> findAllClaims(String organizationId);
	
	/**
	 * Create a Claim entry in the database
	 * @param uri - Claim designated URI
	 * @param name - Claim name
	 * @param description - Claim description (may be empty)
	 * @param o - Organization to which the Claim belongs
	 * @param createdBy - User who creates the record
	 * @return The created Claim
	 * @throws ClaimException - When the Claim with External ID or URI already exists
	 */
	public Claim createClaim(String uri, String name, String description, Organization o, String createdBy) throws ClaimException;
	
	/**
	 * Get all Roles
	 * @param organizationId - Organization External ID
	 * @return
	 */
	public Set<Role> findAllRoles(String organizationId);
	
	/**
	 * Get a Role by its external ID
	 * @param roleExteralId - Role External ID
	 * @param organizationId - Organization External ID
	 * @return
	 */
	public Role findRoleByExternalId(String roleExteralId, String organizationId) throws RoleException;
	
	/**
	 * Create a Role entry in the database
	 * @param uri - Role designated URI
	 * @param name - Role name
	 * @param o - Organization to which the Role belongs
	 * @param createdBy - User who creates the record
	 * @return The created Role
	 * @throws RoleException - When the Role with External ID or URI already exists
	 */
	public Role createRole(String uri, String name, Organization o, String createdBy) throws RoleException;
	
	/**
	 * Get a User via its External Id
	 * @param externalId - User External ID
	 * @param organizationExternalId - Organization External ID
	 * @return the User
	 * @throws UserException - When no User exists
	 */
	public User findUserByExternalId(String externalId, String organizationExternalId) throws UserException;
	
	/**
	 * Create a User entry in the database. Parameters enabled is set to false. Claims ORG and USERNAME are automatically created
	 * @param username - Username of the User
	 * @param password - Plain text Password of the User (will be hashed)
	 * @param administrator - Is the User is admin
	 * @param organization - Organization to which the User belongs
	 * @param createdBy - User who creates the record
	 * @return The created User
	 * @throws UserException - When a User with External Id or email already exists
	 */
	public User createUser(String username, String password, boolean administrator, Organization organization, String createdBy) throws UserException;
	
	/**
	 * Update a User entry in the database. Parameters enabled is set to false. Claims ORG and USERNAME are automatically created
	 * @param externalId - User External ID
	 * @param password - Plain text Password of the User (will be hashed)
	 * @param enabled - Is the User is enabled
	 * @param administrator - Is the User is admin
	 * @param organization - Organization to which the User belongs
	 * @return The updated User
	 * @throws UserException - When a User with External Id does not exist
	 */
	public User updateUser(String externalId, String password, boolean administrator, boolean enabled, Organization organization) throws UserException;
	
	/**
	 * Delete a User entry in the database
	 * @param externalId - User External ID
	 * @param organization - Organization to which the User belongs
	 * @throws UserException - When a User with External Id does not exist
	 */
	public void deleteUser(String externalId, Organization organization) throws UserException;

}
