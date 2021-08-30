package com.tazouxme.idp.application;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.bo.contract.IClaimBo;
import com.tazouxme.idp.bo.contract.IFederationBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IRoleBo;
import com.tazouxme.idp.bo.contract.IUserBo;
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
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.util.defaults.ClaimsDefaults;
import com.tazouxme.idp.util.defaults.RolesDefaults;

public class IdentityProviderApplication implements IIdentityProviderApplication {
	
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
	private IUserBo userBo;
	
	@Override
	public Access createAccess(User user, Application application, Role role, Organization organization) throws AccessException {
		Access access = new Access();
		access.setOrganization(organization);
		access.setUser(user);
		access.setApplication(application);
		access.setRole(role);
		access.setEnabled(true);
		
		accessBo.create(access);
		
		if (access.getOrganization().isFederation()) {
			try {
				createFederation(user, application, organization);
			} catch (FederationException e) {
				throw new AccessException("Cannot create a new Federation for Access", e);
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
		
		return accessBo.update(access);
	}
	
	@Override
	public void deleteAccess(String externalId, Organization organization) throws AccessException {
		Access access = accessBo.findByExternalId(externalId, organization.getExternalId());
		
		try {
			Federation federation = new Federation();
			federation.setUser(access.getUser());
			federation.setApplication(access.getApplication());
			federation.setOrganization(organization);
			federationBo.delete(federation);
		} catch (FederationException e) {
			throw new AccessException("Cannot delete Federation for Access", e);
		}
		
		accessBo.delete(access);
	}
	
	@Override
	public Set<Activation> findActivationsByUser(String userExternalId, String organizationId) {
		return activationBo.findByUser(userExternalId, organizationId);
	}
	
	@Override
	public Activation createActivation(String organizationId, String userId, String step) throws ActivationException {
		Activation activation = new Activation();
		activation.setOrganizationExternalId(organizationId);
		activation.setUserExternalId(userId);
		activation.setStep(step);
		
		return activationBo.create(activation);
	}
	
	@Override
	public Set<Application> findAllApplications(String organizationId) {
		return applicationBo.findAll(organizationId);
	}
	
	@Override
	public Application findApplicationByExternalId(String externalId, String organizationId) throws ApplicationException {
		return applicationBo.findByExternalId(externalId, organizationId);
	}
	
	@Override
	public Application findApplicationByURN(String urn, String organizationId) throws ApplicationException {
		return applicationBo.findByUrn(urn, organizationId);
	}
	
	@Override
	public Application createApplication(String urn, String name, String description, String acsUrl, String logoutUrl, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setUrn(urn);
		application.setName(name);
		application.setDescription(description);
		application.setAssertionUrl(acsUrl);
		application.setLogoutUrl(logoutUrl);
		application.setOrganization(organization);
		
		return applicationBo.create(application);
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
		
		return applicationBo.update(application);
	}
	
	@Override
	public Application updateApplicationClaims(String externalId, Set<Claim> claims, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setExternalId(externalId);
		application.setOrganization(organization);
		application.getClaims().addAll(claims);
		
		return applicationBo.updateClaims(application);
	}
	
	@Override
	public void deleteApplication(String externalId, Organization organization) throws ApplicationException {
		Application application = findApplicationByExternalId(externalId, organization.getExternalId());
		
		for (Access access : application.getAccesses()) {
			try {
				accessBo.delete(access);
			} catch (AccessException e) {
				throw new ApplicationException("Unable to delete Access for Application", e);
			}
		}
		
		for (Federation federation : application.getFederations()) {
			try {
				federationBo.delete(federation);
			} catch (FederationException e) {
				throw new ApplicationException("Unable to delete Federation for Application", e);
			}
		}
		
		applicationBo.delete(application);
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
		return federationBo.findByUserAndURN(userExternalId, urn, organizationExternalId);
	}
	
	@Override
	public Federation createFederation(User user, Application application, Organization organization) throws FederationException {
		Federation federation = new Federation();
		federation.setOrganization(organization);
		federation.setUser(user);
		federation.setApplication(application);
		federation.setEnabled(true);
		
		return federationBo.create(federation);
	}
	
	@Override
	public Organization findOrganizationByExternalId(String externalId) throws OrganizationException {
		return organizationBo.findByExternalId(externalId);
	}
	
	@Override
	public Organization findOrganizationByDomain(String domain) throws OrganizationException {
		return organizationBo.findByDomain(domain);
	}
	
	@Override
	public Organization createOrganization(String code, String domain) throws OrganizationException {
		Organization organization = new Organization();
		organization.setCode(code);
		organization.setName(code);
		organization.setDomain(domain);
		organization.setCertificate("");
		organization.setEnabled(false);
		organization.setFederation(false);
		organization = organizationBo.create(organization);
		
		for (ClaimsDefaults claim : ClaimsDefaults.values()) {
			try {
				createClaim(claim.getUri(), claim.getName(), claim.getDescription(), organization);
			} catch (ClaimException e) {
				throw new OrganizationException("Unable to create new default Claim for Organization", e);
			}
		}
		
		for (RolesDefaults role : RolesDefaults.values()) {
			try {
				createRole(role.getUri(), role.getName(), organization);
			} catch (RoleException e) {
				throw new OrganizationException("Unable to create new default Role for Organization", e);
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
		
		return organizationBo.update(organization);
	}
	
	@Override
	public Organization setCertificate(String id, String certificate) throws OrganizationException {
		Organization organization = findOrganizationByExternalId(id);
		organization.setCertificate(certificate);
		
		return organizationBo.updateCertificate(organization);
	}
	
	@Override
	public Organization deleteCertificate(String id) throws OrganizationException {
		Organization organization = findOrganizationByExternalId(id);
		organization.setCertificate("");
		
		return organizationBo.updateCertificate(organization);
	}
	
	@Override
	public Set<Claim> findAllClaims(String organizationId) {
		return claimBo.findAll(organizationId);
	}
	
	@Override
	public Claim createClaim(String uri, String name, String description, Organization o) throws ClaimException {
		Claim claim = new Claim();
		claim.setUri(uri);
		claim.setName(name);
		claim.setDescription(description);
		claim.setOrganization(o);
		
		return claimBo.create(claim);
	}
	
	@Override
	public Set<Role> findAllRoles(String organizationId) {
		return roleBo.findAll(organizationId);
	}
	
	@Override
	public Role findRoleByExternalId(String roleExteralId, String organizationId) throws RoleException {
		return roleBo.findByExternalId(roleExteralId, organizationId);
	}
	
	@Override
	public Role createRole(String uri, String name, Organization o) throws RoleException {
		Role role = new Role();
		role.setUri(uri);
		role.setName(name);
		role.setOrganization(o);
		
		return roleBo.create(role);
	}
	
	@Override
	public User findUserByExternalId(String externalId, String organizationExternalId) throws UserException {
		return userBo.findByExternalId(externalId, organizationExternalId);
	}
	
	@Override
	public User createUser(String username, String password, boolean administrator, Organization organization) throws UserException {
		User user = new User();
		user.setUsername(username);
		user.setEmail(username + "@" + organization.getDomain());
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(6)));
		user.setAdministrator(administrator);
		user.setEnabled(false);
		user.setOrganization(organization);
		
		try {
			user.getDetails().add(createUserDetails(IdentityProviderConstants.SAML_CLAIM_ORGANIZATION, organization.getDomain(), organization.getExternalId(), user));
			user.getDetails().add(createUserDetails(IdentityProviderConstants.SAML_CLAIM_EMAIL, user.getEmail(), organization.getExternalId(), user));
		} catch (ClaimException e) {
			throw new UserException("Unable to create new default Claim for User", e);
		}
		
		return userBo.create(user);
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
		
		return userBo.update(user);
	}
	
	@Override
	public void deleteUser(String externalId, Organization organization) throws UserException {
		User user = findUserByExternalId(externalId, organization.getExternalId());
		
		for (Access access : user.getAccesses()) {
			try {
				accessBo.delete(access);
			} catch (AccessException e) {
				throw new UserException("Unable to delete Access for User", e);
			}
		}
		
		for (Activation activation : findActivationsByUser(organization.getExternalId(), externalId)) {
			try {
				activationBo.delete(activation);
			} catch (ActivationException e) {
				throw new UserException("Unable to delete Activation for User", e);
			}
		}
		
		for (Federation federation : findFederationsByUser(externalId, organization.getExternalId())) {
			try {
				federationBo.delete(federation);
			} catch (FederationException e) {
				throw new UserException("Unable to delete Federation for User", e);
			}
		}
		
		userBo.delete(user);
	}
	
	private UserDetails createUserDetails(String key, String value, String organizationId, User user) throws ClaimException {
		UserDetails details = new UserDetails();
		details.setClaim(claimBo.findByURI(key, organizationId));
		details.setClaimValue(value);
		details.setCreationDate(new Date().getTime());
		details.setUser(user);
		
		return details;
	}

}
