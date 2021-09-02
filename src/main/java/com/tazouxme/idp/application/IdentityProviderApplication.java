package com.tazouxme.idp.application;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.AccessException;
import com.tazouxme.idp.application.exception.ActivationException;
import com.tazouxme.idp.application.exception.ApplicationException;
import com.tazouxme.idp.application.exception.ClaimException;
import com.tazouxme.idp.application.exception.FederationException;
import com.tazouxme.idp.application.exception.OrganizationException;
import com.tazouxme.idp.application.exception.RoleException;
import com.tazouxme.idp.application.exception.SessionException;
import com.tazouxme.idp.application.exception.StoreException;
import com.tazouxme.idp.application.exception.UserException;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;
import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.bo.contract.IClaimBo;
import com.tazouxme.idp.bo.contract.IFederationBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IRoleBo;
import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.bo.contract.IStoreBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.Role;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.model.Store;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.util.defaults.ClaimsDefaults;
import com.tazouxme.idp.util.defaults.RolesDefaults;

public class IdentityProviderApplication implements IIdentityProviderApplication {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IAccessBo accessBo;
	
	@Autowired
	private IActivationBo activationBo;
	
	@Autowired
	private IApplicationBo applicationBo;
	
	@Autowired
	private IFederationBo federationBo;
	
	@Autowired
	private IOrganizationBo organizationBo;
	
	@Autowired
	private IClaimBo claimBo;
	
	@Autowired
	private IRoleBo roleBo;
	
	@Autowired
	private ISessionBo sessionBo;
	
	@Autowired
	private IStoreBo storeBo;
	
	@Autowired
	private IUserBo userBo;
	
	@Override
	public Access findAccessByExternalId(String externalId, String organizationId) throws AccessException {
		try {
			return accessBo.findByExternalId(externalId, organizationId);
		} catch (Exception e) {
			throw new AccessException(IdentityProviderExceptionCode.ACCESS_NOT_FOUND, "Unable to find Access by its ID", e);
		}
	}
	
	@Override
	public Access findAccessByUserAndURN(String externalId, String urn, String organizationId) throws AccessException {
		try {
			return accessBo.findByUserAndURN(externalId, urn, organizationId);
		} catch (Exception e) {
			throw new AccessException(IdentityProviderExceptionCode.ACCESS_NOT_FOUND, "Unable to find Access by User and Application's URN", e);
		}
	}
	
	@Override
	public Access createAccess(User user, Application application, Role role, Organization organization, String createdBy) throws AccessException {
		Access access = new Access();
		access.setOrganization(organization);
		access.setUser(user);
		access.setApplication(application);
		access.setRole(role);
		access.setEnabled(true);
		access.setCreatedBy(createdBy);
		
		try {
			accessBo.create(access);
		} catch (Exception e) {
			throw new AccessException(IdentityProviderExceptionCode.ACCESS_ALREADY_EXISTS, "Unable to create new Access", e);
		}
		
		if (access.getOrganization().isFederation()) {
			try {
				createFederation(user, application, organization, createdBy);
			} catch (FederationException e) {
				throw new AccessException(e.getCode(), "Cannot create a new Federation for Access", e);
			}
		}
		
		return access;
	}
	
	@Override
	public Access updateAccess(String externalId, boolean enabled, Organization organization) throws AccessException {
		Access access = new Access();
		access.setExternalId(externalId);
		access.setEnabled(enabled);
		access.setOrganization(organization);

		try {
			return accessBo.update(access);
		} catch (Exception e) {
			throw new AccessException(IdentityProviderExceptionCode.ACCESS_NOT_FOUND, "Unable to update Access", e);
		}
	}
	
	@Override
	public void deleteAccess(String externalId, Organization organization) throws AccessException {
		Access access = findAccessByExternalId(externalId, organization.getExternalId());
		
		try {
			Federation federation = findFederationByUserAndURN(access.getUser().getExternalId(), access.getApplication().getUrn(), organization.getExternalId());
			deleteFederation(federation, organization);
		} catch (FederationException e) {
			throw new AccessException(e.getCode(), "Cannot delete Federation for Access", e);
		}
		
		try {
			accessBo.delete(access);
		} catch (Exception e) {
			throw new AccessException(IdentityProviderExceptionCode.ACCESS_NOT_FOUND, "Unable to delete Access", e);
		}
	}
	
	@Override
	public Activation findActivationByExternalId(String externalId) throws ActivationException {
		try {
			return activationBo.findByExternalId(externalId);
		} catch (Exception e) {
			throw new ActivationException(IdentityProviderExceptionCode.ACTIVATION_NOT_FOUND, "Unable to find an Activation by its ID", e);
		}
	}
	
	@Override
	public Activation findActivation(String userExternalId, String organizationId, String step) throws ActivationException {
		try {
			return activationBo.find(organizationId, userExternalId, step);
		} catch (Exception e) {
			throw new ActivationException(IdentityProviderExceptionCode.ACTIVATION_NOT_FOUND, "Unable to find an Activation by User and step", e);
		}
	}
	
	@Override
	public Set<Activation> findActivationsByUser(String userExternalId, String organizationId) {
		return activationBo.findByUser(userExternalId, organizationId);
	}
	
	@Override
	public Activation createActivation(String organizationId, String userId, String step, String createdBy) throws ActivationException {
		Activation activation = new Activation();
		activation.setOrganizationExternalId(organizationId);
		activation.setUserExternalId(userId);
		activation.setStep(step);
		activation.setCreatedBy(createdBy);
		
		try {
			return activationBo.create(activation);
		} catch (Exception e) {
			throw new ActivationException(IdentityProviderExceptionCode.ACTIVATION_ALREADY_EXISTS, "Unable to create Activation", e);
		}
	}
	
	@Override
	public void deleteActivation(String externalId, Organization organization) throws ActivationException {
		Activation activation = new Activation();
		activation.setExternalId(externalId);
		activation.setOrganizationExternalId(organization.getExternalId());
		
		try {
			activationBo.delete(activation);
		} catch (Exception e) {
			throw new ActivationException(IdentityProviderExceptionCode.ACTIVATION_NOT_FOUND, "Unable to delete Activation", e);
		}
	}
	
	@Override
	public Set<Application> findAllApplications(String organizationId) {
		return applicationBo.findAll(organizationId);
	}
	
	@Override
	public Application findApplicationByExternalId(String externalId, String organizationId) throws ApplicationException {
		try {
			return applicationBo.findByExternalId(externalId, organizationId);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND, "Unable to find Application by its ID", e);
		}
	}
	
	@Override
	public Application findApplicationByURN(String urn, String organizationId) throws ApplicationException {
		try {
			return applicationBo.findByUrn(urn, organizationId);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND, "Unable to find Application by its URN", e);
		}
	}
	
	@Override
	public Application createApplication(String urn, String name, String description, String acsUrl, String logoutUrl, Organization organization, String createdBy) throws ApplicationException {
		Application application = new Application();
		application.setUrn(urn);
		application.setName(name);
		application.setDescription(description);
		application.setAssertionUrl(acsUrl);
		application.setLogoutUrl(logoutUrl);
		application.setOrganization(organization);
		application.setCreatedBy(createdBy);

		try {
			return applicationBo.create(application);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_ALREADY_EXISTS, "Unable to create new Application", e);
		}
	}
	
	@Override
	public Application updateApplication(String externalId, String urn, String name, String description, String acsUrl, String logoutUrl, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setExternalId(externalId);
		application.setUrn(urn);
		application.setName(name);
		application.setDescription(description);
		application.setAssertionUrl(acsUrl);
		application.setLogoutUrl(logoutUrl);
		application.setOrganization(organization);

		try {
			return applicationBo.update(application);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND, "Unable to update Application", e);
		}
	}
	
	@Override
	public Application updateApplicationClaims(String externalId, Set<Claim> claims, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setExternalId(externalId);
		application.setOrganization(organization);
		application.getClaims().addAll(claims);

		try {
			return applicationBo.updateClaims(application);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND, "Unable to update Application's Claims", e);
		}
	}
	
	@Override
	public void deleteApplication(String externalId, Organization organization) throws ApplicationException {
		Application application = findApplicationByExternalId(externalId, organization.getExternalId());
		
		for (Access access : application.getAccesses()) {
			try {
				deleteAccess(access.getExternalId(), organization);
			} catch (AccessException e) {
				throw new ApplicationException(e.getCode(), "Unable to delete Access for Application", e);
			}
		}

		try {
			applicationBo.delete(application);
		} catch (Exception e) {
			throw new ApplicationException(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND, "Unable to delete Application", e);
		}
	}
	
	@Override
	public Set<Federation> findFederationsByURN(String urn, String organizationExternalId) {
		return federationBo.findByURN(urn, organizationExternalId);
	}
	
	@Override
	public Set<Federation> findFederationsByUser(String userExternalId, String organizationExternalId) {
		return federationBo.findByUser(userExternalId, organizationExternalId);
	}
	
	@Override
	public Federation findFederationByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws FederationException {
		try {
			return federationBo.findByUserAndURN(userExternalId, urn, organizationExternalId);
		} catch (Exception e) {
			throw new FederationException(IdentityProviderExceptionCode.FEDERATION_NOT_FOUND, "Unable to find Federation by User and Application URN", e);
		}
	}
	
	@Override
	public Federation createFederation(User user, Application application, Organization organization, String createdBy) throws FederationException {
		Federation federation = new Federation();
		federation.setOrganization(organization);
		federation.setUser(user);
		federation.setApplication(application);
		federation.setEnabled(true);
		federation.setCreatedBy(createdBy);

		try {
			return federationBo.create(federation);
		} catch (Exception e) {
			throw new FederationException(IdentityProviderExceptionCode.FEDERATION_ALREADY_EXISTS, "Unable to create new Federation", e);
		}
	}
	
	@Override
	public void deleteFederation(Federation federation, Organization organization) throws FederationException {
		federation.setOrganization(organization);

		try {
			federationBo.delete(federation);
		} catch (Exception e) {
			throw new FederationException(IdentityProviderExceptionCode.FEDERATION_NOT_FOUND, "Unable to delete Federation", e);
		}
	}
	
	@Override
	public Organization findOrganizationByExternalId(String externalId) throws OrganizationException {
		try {
			return organizationBo.findByExternalId(externalId);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_NOT_FOUND, "Unable to find Organization by its ID", e);
		}
	}
	
	@Override
	public Organization findOrganizationByDomain(String domain) throws OrganizationException {
		try {
			return organizationBo.findByDomain(domain);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_NOT_FOUND, "Unable to find Organization by its Domain", e);
		}
	}
	
	@Override
	public Organization createOrganization(String code, String domain, String createdBy) throws OrganizationException {
		Organization organization = new Organization();
		organization.setCode(code);
		organization.setName(code);
		organization.setDomain(domain);
		organization.setCertificate(null);
		organization.setEnabled(false);
		organization.setFederation(false);
		organization.setCreatedBy(createdBy);

		try {
			organization = organizationBo.create(organization);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_ALREADY_EXISTS, "Unable to create new Organization", e);
		}
		
		for (ClaimsDefaults claim : ClaimsDefaults.values()) {
			try {
				createClaim(claim.getUri(), claim.getName(), claim.getDescription(), organization, createdBy);
			} catch (ClaimException e) {
				throw new OrganizationException(e.getCode(), "Unable to create new default Claim for Organization", e);
			}
		}
		
		for (RolesDefaults role : RolesDefaults.values()) {
			try {
				createRole(role.getUri(), role.getName(), organization, createdBy);
			} catch (RoleException e) {
				throw new OrganizationException(e.getCode(), "Unable to create new default Role for Organization", e);
			}
		}
		
		return organization;
	}
	
	@Override
	public Organization updateOrganization(String id, String name, String description, boolean federation) throws OrganizationException {
		Organization organization = findOrganizationByExternalId(id);
		organization.setName(name);
		organization.setDescription(description);
		
		if (organization.isFederation() && !federation || !organization.isFederation() && federation) {
			organization.setFederation(federation);
		}

		try {
			return organizationBo.update(organization);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_NOT_FOUND, "Unable to update Organization", e);
		}
	}
	
	@Override
	public Organization setCertificate(String id, String certificate) throws OrganizationException {
		Organization organization = new Organization();
		organization.setExternalId(id);
		organization.setCertificate(certificate.getBytes());

		try {
			return organizationBo.updateCertificate(organization);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_NOT_FOUND, "Unable to update Organization's Certificate", e);
		}
	}
	
	@Override
	public Organization deleteCertificate(String id) throws OrganizationException {
		Organization organization = new Organization();
		organization.setExternalId(id);
		organization.setCertificate(null);

		try {
			return organizationBo.updateCertificate(organization);
		} catch (Exception e) {
			throw new OrganizationException(IdentityProviderExceptionCode.ORGANIZATION_NOT_FOUND, "Unable to delete Organization's Certificate", e);
		}
	}
	
	@Override
	public Set<Claim> findAllClaims(String organizationId) {
		return claimBo.findAll(organizationId);
	}
	
	@Override
	public Claim findClaimByURI(String uri, String organizationId) throws ClaimException {
		try {
			return claimBo.findByURI(uri, organizationId);
		} catch (Exception e) {
			throw new ClaimException(IdentityProviderExceptionCode.CLAIM_NOT_FOUND, "Unable to find Claim by its URI", e);
		}
	}
	
	@Override
	public Claim createClaim(String uri, String name, String description, Organization o, String createdBy) throws ClaimException {
		Claim claim = new Claim();
		claim.setUri(uri);
		claim.setName(name);
		claim.setDescription(description);
		claim.setOrganization(o);
		claim.setCreatedBy(createdBy);
		
		try {
			return claimBo.create(claim);
		} catch (Exception e) {
			throw new ClaimException(IdentityProviderExceptionCode.CLAIM_ALREADY_EXISTS, "Unable to create new Claim", e);
		}
	}
	
	@Override
	public Set<Role> findAllRoles(String organizationId) {
		return roleBo.findAll(organizationId);
	}
	
	@Override
	public Role findRoleByExternalId(String roleExteralId, String organizationId) throws RoleException {
		try {
			return roleBo.findByExternalId(roleExteralId, organizationId);
		} catch (Exception e) {
			throw new RoleException(IdentityProviderExceptionCode.ROLE_NOT_FOUND, "Unable to find Role by its ID", e);
		}
	}
	
	@Override
	public Role createRole(String uri, String name, Organization o, String createdBy) throws RoleException {
		Role role = new Role();
		role.setUri(uri);
		role.setName(name);
		role.setOrganization(o);
		role.setCreatedBy(createdBy);

		try {
			return roleBo.create(role);
		} catch (Exception e) {
			throw new RoleException(IdentityProviderExceptionCode.ROLE_ALREADY_EXISTS, "Unable to create new Role", e);
		}
	}
	
	@Override
	public Session findSession(String userExternalId, String organizationId) throws SessionException {
		try {
			return sessionBo.find(organizationId, userExternalId);
		} catch (Exception e) {
			throw new SessionException(IdentityProviderExceptionCode.SESSION_NOT_FOUND, "Unable to find Session for User", e);
		}
	}
	
	@Override
	public Session createSession(User user, Organization organization, String createdBy) throws SessionException {
		Session session = new Session();
		session.setUserExternalId(user.getExternalId());
		session.setOrganizationExternalId(organization.getExternalId());
		session.setCreatedBy(createdBy);
		
		try {
			try {
				deleteSession(user, organization);
			} catch (SessionException e) {
				logger.info("No Session found... proceeding");
			}
			
			return sessionBo.create(session);
		} catch (Exception e) {
			throw new SessionException(IdentityProviderExceptionCode.SESSION_ALREADY_EXISTS, "Unable to create new Session", e);
		}
	}
	
	@Override
	public void deleteSession(User user, Organization organization) throws SessionException {
		Session session = new Session();
		session.setUserExternalId(user.getExternalId());
		session.setOrganizationExternalId(organization.getExternalId());
		
		try {
			sessionBo.delete(session);
		} catch (Exception e) {
			throw new SessionException(IdentityProviderExceptionCode.SESSION_NOT_FOUND, "Unable to delete Session", e);
		}
	}
	
	@Override
	public User findUserByEmail(String email, String organizationExternalId) throws UserException {
		try {
			return userBo.findByEmail(email, organizationExternalId);
		} catch (Exception e) {
			throw new UserException(IdentityProviderExceptionCode.USER_NOT_FOUND, "Unable to find User by its Email", e);
		}
	}
	
	@Override
	public User findUserByExternalId(String externalId, String organizationExternalId) throws UserException {
		try {
			return userBo.findByExternalId(externalId, organizationExternalId);
		} catch (Exception e) {
			throw new UserException(IdentityProviderExceptionCode.USER_NOT_FOUND, "Unable to find User by its ID", e);
		}
	}
	
	@Override
	public User createUser(String username, String password, boolean administrator, Organization organization, String createdBy) throws UserException {
		User user = new User();
		user.setUsername(username);
		user.setEmail(username + "@" + organization.getDomain());
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(6)));
		user.setAdministrator(administrator);
		user.setEnabled(false);
		user.setOrganization(organization);
		user.setCreatedBy(createdBy);
		
		try {
			user.getDetails().add(createUserDetails(IdentityProviderConstants.SAML_CLAIM_ORGANIZATION, organization.getDomain(), organization.getExternalId(), user, createdBy));
			user.getDetails().add(createUserDetails(IdentityProviderConstants.SAML_CLAIM_EMAIL, user.getEmail(), organization.getExternalId(), user, createdBy));
		} catch (ClaimException e) {
			throw new UserException(e.getCode(), "Unable to create new default Claim for User", e);
		}
		
		try {
			return userBo.create(user);
		} catch (Exception e) {
			throw new UserException(IdentityProviderExceptionCode.USER_ALREADY_EXISTS, "Unable to create new User", e);
		}
	}
	
	@Override
	public User updateUser(String externalId, String password, boolean administrator, boolean enabled, Organization organization) throws UserException {
		User user = new User();
		user.setExternalId(externalId);
		user.setAdministrator(administrator);
		user.setEnabled(enabled);
		user.setOrganization(organization);
		
		if (!StringUtils.isBlank(password)) {
			user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(6)));
		}
		
		try {
			return userBo.update(user);
		} catch (Exception e) {
			throw new UserException(IdentityProviderExceptionCode.USER_NOT_FOUND, "Unable to update User", e);
		}
	}
	
	@Override
	public void deleteUser(String externalId, Organization organization) throws UserException {
		User user = findUserByExternalId(externalId, organization.getExternalId());
		
		for (Access access : user.getAccesses()) {
			try {
				deleteAccess(access.getExternalId(), organization);
			} catch (AccessException e) {
				throw new UserException(e.getCode(), "Unable to delete Access for User", e);
			}
		}
		
		for (Activation activation : findActivationsByUser(externalId, organization.getExternalId())) {
			try {
				deleteActivation(activation.getExternalId(), organization);
			} catch (ActivationException e) {
				throw new UserException(e.getCode(), "Unable to delete Activation for User", e);
			}
		}
		
		for (Federation federation : findFederationsByUser(externalId, organization.getExternalId())) {
			try {
				deleteFederation(federation, organization);
			} catch (FederationException e) {
				throw new UserException(e.getCode(), "Unable to delete Federation for User", e);
			}
		}

		try {
			userBo.delete(user);
		} catch (Exception e) {
			throw new UserException(IdentityProviderExceptionCode.USER_NOT_FOUND, "Unable to delete User", e);
		}
	}
	
	@Override
	public Store findStoreByKey(String context, String key, String organizationId) throws StoreException {
		try {
			return storeBo.findByKey(context, key, organizationId);
		} catch (Exception e) {
			throw new StoreException(IdentityProviderExceptionCode.STORE_NOT_FOUND, "Unable to find Store", e);
		}
	}
	
	@Override
	public Store findStoreByKeyAndVersion(String context, String key, long version, String organizationId) throws StoreException {
		try {
			return storeBo.findByKeyAndVersion(context, key, version, organizationId);
		} catch (Exception e) {
			throw new StoreException(IdentityProviderExceptionCode.STORE_NOT_FOUND, "Unable to find Store", e);
		}
	}
	
	@Override
	public Store createStore(String context, String key, String value, Long expiration, Organization organization, String createdBy) throws StoreException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setStoreValue(value.getBytes());
		store.setExpiration(expiration);
		store.setVersion(1L);
		store.setOrganization(organization);
		store.setCreatedBy(createdBy);
		
		try {
			return storeBo.create(store);
		} catch (Exception e) {
			throw new StoreException(IdentityProviderExceptionCode.STORE_ALREADY_EXISTS, "Unable to create new Store", e);
		}
	}
	
	@Override
	public Store updateStore(Store store, Organization organization) throws StoreException {
		store.setOrganization(organization);
		
		try {
			return storeBo.update(store);
		} catch (Exception e) {
			throw new StoreException(IdentityProviderExceptionCode.STORE_NOT_FOUND, "Unable to update Store", e);
		}
	}
	
	@Override
	public void deleteStore(Store store, Organization organization) throws StoreException {
		store.setOrganization(organization);
		
		try {
			storeBo.delete(store);
		} catch (Exception e) {
			throw new StoreException(IdentityProviderExceptionCode.STORE_NOT_FOUND, "Unable to delete Store", e);
		}
	}
	
	private UserDetails createUserDetails(String key, String value, String organizationId, User user, String createdBy) throws ClaimException {
		UserDetails details = new UserDetails();
		details.setClaim(findClaimByURI(key, organizationId));
		details.setClaimValue(value);
		details.setCreationDate(new Date().getTime());
		details.setCreatedBy(createdBy);
		details.setStatus(1);
		details.setUser(user);
		
		return details;
	}

}
