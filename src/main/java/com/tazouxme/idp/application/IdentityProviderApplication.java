package com.tazouxme.idp.application;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.bo.contract.IClaimBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IRoleBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Claim;
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
	private IOrganizationBo organizationBo;
	
	@Autowired
	private IClaimBo claimBo;
	
	@Autowired
	private IRoleBo roleBo;
	
	@Autowired
	private IUserBo userBo;
	
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
		return applicationBo.findByExternalId(externalId);
	}
	
	@Override
	public Application findApplicationByURN(String urn, String organizationId) throws ApplicationException {
		return applicationBo.findByUrn(urn, organizationId);
	}
	
	@Override
	public Application createApplication(String urn, String name, String description, String acsUrl, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setUrn(urn);
		application.setName(name);
		application.setDescription(description);
		application.setAssertionUrl(acsUrl);
		application.setOrganization(organization);
		
		return applicationBo.create(application);
	}
	
	@Override
	public Application updateApplication(String externalId, String urn, String name, String description, String acsUrl, Organization organization) throws ApplicationException {
		Application application = new Application();
		application.setExternalId(externalId);
		application.setUrn(urn);
		application.setName(name);
		application.setDescription(description);
		application.setAssertionUrl(acsUrl);
		application.setOrganization(organization);
		
		return applicationBo.update(application);
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
		
		for (Claim claim : application.getClaims()) {
			try {
				claimBo.delete(claim);
			} catch (ClaimException e) {
				throw new ApplicationException("Unable to delete Claim for Application", e);
			}
		}
		
		applicationBo.delete(application);
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
		organization.setPublicKey("");
		organization.setEnabled(false);
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
	public Claim createClaim(String uri, String name, String description, Organization o) throws ClaimException {
		Claim claim = new Claim();
		claim.setUri(uri);
		claim.setName(name);
		claim.setDescription(description);
		claim.setOrganization(o);
		
		return claimBo.create(claim);
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
			user.getDetails().add(createUserDetails(IdentityProviderConstants.SAML_CLAIM_ORGANIZATION, organization.getCode(), organization.getExternalId(), user));
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
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(6)));
		user.setAdministrator(administrator);
		user.setEnabled(enabled);
		user.setOrganization(organization);
		
		return userBo.update(user);
	}
	
	@Override
	public void deleteUser(String externalId, Organization organization) throws UserException {
		User user = new User();
		user.setExternalId(externalId);
		user.setOrganization(organization);
		
		userBo.delete(user);
	}
	
	private UserDetails createUserDetails(String key, String value, String organizationId, User user) throws ClaimException {
		UserDetails details = new UserDetails();
		details.setClaim(claimBo.findByURI(key, organizationId));
		details.setClaimValue(value);
		details.setCreationDate(new Date().getTime());
		details.setUser(user);;
		
		return details;
	}

}