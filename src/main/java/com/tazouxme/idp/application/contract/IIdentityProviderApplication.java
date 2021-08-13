package com.tazouxme.idp.application.contract;

import java.util.Set;

import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.Role;
import com.tazouxme.idp.model.User;

public interface IIdentityProviderApplication {
	
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
	 * @return The created Activation
	 * @throws ActivationException - When an Activation with the same External ID exists
	 */
	public Activation createActivation(String organizationId, String userId, String step) throws ActivationException;
	
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
	 * @param organization - Organization to which the Application belongs
	 * @return The created Application
	 * @throws ApplicationException - When an Application with External ID already exists
	 */
	public Application createApplication(String urn, String name, String description, String acsUrl, Organization organization) throws ApplicationException;
	
	/**
	 * Update an Application entry in the database
	 * @param externalId - Application's External Id
	 * @param urn - Application's URN
	 * @param name - Application's name
	 * @param description - Application's description (may be empty)
	 * @param acsUrl - Application's Assertion Consumer Service URL
	 * @param organization - Organization to which the Application belongs
	 * @return The updated Application
	 * @throws ApplicationException - When an Application with External ID does not exist
	 */
	public Application updateApplication(String externalId, String urn, String name, String description, String acsUrl, Organization organization) throws ApplicationException;
	
	/**
	 * Delete an Application entry in the database
	 * @param externalId - Application's External Id
	 * @param organization - Organization to which the Application belongs
	 * @throws ApplicationException - When an Application with External ID does not exist
	 */
	public void deleteApplication(String externalId, Organization organization) throws ApplicationException;
	
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
	 * @return The created Organization
	 * @throws OrganizationException - When the Organization with External ID or domain already exists
	 */
	public Organization createOrganization(String code, String domain) throws OrganizationException;
	
	/**
	 * Update an Organization entry in the database
	 * @param id - Organization External ID
	 * @param name - Organization name
	 * @param description - Organization description (may be null)
	 * @param publicKey - Organization encoded PublicKey from Certificate (may be null)
	 * @return The updated Organization
	 * @throws OrganizationException - When the Organization with External ID or domain does not exist
	 */
	public Organization updateOrganization(String id, String name, String description, String publicKey) throws OrganizationException;
	
	/**
	 * Create a Claim entry in the database
	 * @param uri - Claim designated URI
	 * @param name - Claim name
	 * @param description - Claim description (may be empty)
	 * @param o - Organization to which the Claim belongs
	 * @return The created Claim
	 * @throws ClaimException - When the Claim with External ID or URI already exists
	 */
	public Claim createClaim(String uri, String name, String description, Organization o) throws ClaimException;
	
	/**
	 * Create a Role entry in the database
	 * @param uri - Role designated URI
	 * @param name - Role name
	 * @param o - Organization to which the Role belongs
	 * @return The created Role
	 * @throws RoleException - When the Role with External ID or URI already exists
	 */
	public Role createRole(String uri, String name, Organization o) throws RoleException;
	
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
	 * @return The created User
	 * @throws UserException - When a User with External Id or email already exists
	 */
	public User createUser(String username, String password, boolean administrator, Organization organization) throws UserException;
	
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
