package com.tazouxme.idp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.activation.sender.ActivationSender;
import com.tazouxme.idp.activation.sender.MailActivationSender;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.ApplicationException;
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
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserIdentity;
import com.tazouxme.idp.service.contract.IIdentityProviderService;
import com.tazouxme.idp.service.entity.AccessEntity;
import com.tazouxme.idp.service.entity.ApplicationEntity;
import com.tazouxme.idp.service.entity.ClaimEntity;
import com.tazouxme.idp.service.entity.FederationEntity;
import com.tazouxme.idp.service.entity.OrganizationEntity;
import com.tazouxme.idp.service.entity.RoleEntity;
import com.tazouxme.idp.service.entity.UserDetailsEntity;
import com.tazouxme.idp.service.entity.UserEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;
import com.tazouxme.idp.util.SanitizerUtils;

public class IdentityProviderService implements IIdentityProviderService {
	
	@Autowired
	private IdentityProviderConfiguration configuration;
	
	@Autowired
	private IIdentityProviderApplication idpApplication;
	
	@Override
	public Response getOrganization() {
		try {
			Organization organization = idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId());
			
			OrganizationEntity entity = new OrganizationEntity();
			entity.setId(organization.getExternalId());
			entity.setCode(organization.getCode());
			entity.setDomain(organization.getDomain());
			entity.setName(organization.getName());
			entity.setDescription(organization.getDescription());
			entity.setCertificate(organization.getCertificate());
			entity.setFederation(organization.isFederation());
			entity.setCreationDate(organization.getCreationDate());
			
			for (Claim claim : organization.getClaims()) {
				ClaimEntity claimEntity = new ClaimEntity();
				claimEntity.setId(claim.getExternalId());
				claimEntity.setUri(claim.getUri());
				claimEntity.setName(claim.getName());
				claimEntity.setDescription(claim.getDescription());
				
				entity.getClaims().add(claimEntity);
			}
			
			for (Role role : organization.getRoles()) {
				RoleEntity roleEntity = new RoleEntity();
				roleEntity.setId(role.getExternalId());
				roleEntity.setUri(role.getUri());
				roleEntity.setName(role.getName());
				
				entity.getRoles().add(roleEntity);
			}
			
			return Response.ok(entity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("ORG_NOT_FOUND");
			exceptionEntity.setMessage("Organization not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}
	
	@Override
	public Response updateOrganization(OrganizationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId(), entity.getName());
		validationResult.getValidations().addAll(SanitizerUtils.sanitizeEquals(entity.getId(), findUserIdentity().getOrganizationId()).getValidations());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.updateOrganization(entity.getId(), entity.getName(), entity.getDescription(), entity.isFederation());
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("ORG_NOT_FOUND");
			exceptionEntity.setMessage("Organization not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@Override
	public Response setCertificate(OrganizationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmptyCertificate(entity.getCertificate());
		validationResult.getValidations().addAll(SanitizerUtils.sanitizeEquals(entity.getId(), findUserIdentity().getOrganizationId()).getValidations());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.setCertificate(findUserIdentity().getOrganizationId(), entity.getCertificate());
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("ORG_NOT_FOUND");
			exceptionEntity.setMessage("Organization not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@Override
	public Response deleteCertificate(OrganizationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeEmpty(entity.getCertificate());
		validationResult.getValidations().addAll(SanitizerUtils.sanitizeEquals(entity.getId(), findUserIdentity().getOrganizationId()).getValidations());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.deleteCertificate(findUserIdentity().getOrganizationId());
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("ORG_NOT_FOUND");
			exceptionEntity.setMessage("Organization not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(204).build();
	}
	
	@Override
	public Response getUsers() {
		Set<UserEntity> entities = new HashSet<>();
		
		try {
			for (User user : idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()).getUsers()) {
				UserEntity entity = new UserEntity();
				entity.setId(user.getExternalId());
				entity.setEmail(user.getEmail());
				entity.setUsername(user.getUsername());
				entity.setEnabled(user.isEnabled());
				entity.setAdministrator(user.isAdministrator());
				
				entities.add(entity);
			}
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("ORG_NOT_FOUND");
			exceptionEntity.setMessage("Organization not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.ok(entities).build();
	}
	
	@Override
	public Response getUser(String id) {
		UserEntity entity = new UserEntity();
		
		try {
			User user = idpApplication.findUserByExternalId(id, findUserIdentity().getOrganizationId());
			entity.setId(user.getExternalId());
			entity.setEmail(user.getEmail());
			entity.setUsername(user.getUsername());
			entity.setEnabled(user.isEnabled());
			entity.setAdministrator(user.isAdministrator());
			
			for (UserDetails details : user.getDetails()) {
				ClaimEntity claimEntity = new ClaimEntity();
				claimEntity.setId(details.getClaim().getExternalId());
				claimEntity.setUri(details.getClaim().getUri());
				claimEntity.setName(details.getClaim().getName());
				claimEntity.setDescription(details.getClaim().getDescription());

				UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
				userDetailsEntity.setClaim(claimEntity);
				userDetailsEntity.setClaimValue(details.getClaimValue());
				userDetailsEntity.setCreationDate(details.getCreationDate());
				
				entity.getDetails().add(userDetailsEntity);
			}
		} catch (UserException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("USER_NOT_FOUND");
			exceptionEntity.setMessage("User not found");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.ok(entity).build();
	}
	
	@Override
	public Response createUser(HttpServletRequest request, UserEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getUsername());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
	
		ResponseBuilder responseBuilder = Response.status(201);
		
		try {
			User user = idpApplication.createUser(entity.getUsername(), "changeit", entity.isAdministrator(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			
			try {
				Activation activation = idpApplication.createActivation(user.getOrganization().getExternalId(), user.getExternalId(), IdentityProviderConstants.ACTIVATION_CONST_PASSWORD);
				String link = generateActivationAccess(request, user, activation);
				
				String mailUsername = request.getServletContext().getInitParameter("mail-username");
				String mailPassword = request.getServletContext().getInitParameter("mail-password");
				
				if (!StringUtils.isBlank(mailUsername) && !StringUtils.isBlank(mailPassword)) {
					ActivationSender sender = new MailActivationSender(mailUsername, mailPassword);
					
						if (!sender.send(link, user)) {
							return Response.status(406).entity(new SanitizerValidationImpl(Severity.ERROR, "Unable to send the activation link")).build();
						}
				} else {
					// add Header to response
					responseBuilder.header(IdentityProviderConstants.AUTH_HEADER_ACTIVATION_TOKEN, link);
				}
			} catch (ActivationException e) {
				return Response.status(406).entity(new SanitizerValidationImpl(Severity.ERROR, "Unable to send the create link")).build();
			} catch (ServletException | IOException e) {
				return Response.status(406).entity(new SanitizerValidationImpl(Severity.ERROR, "Unable to send the activation link")).build();
			}
			
			entity.setId(user.getExternalId());
			entity.setEmail(user.getEmail());
			entity.setEnabled(user.isEnabled());
		} catch (UserException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(409).entity(exceptionEntity).build();
		} catch (OrganizationException  e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return responseBuilder.entity(entity).build();
	}
	
	@Override
	public Response updateUser(UserEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId());
		validationResult.getValidations().addAll(SanitizerUtils.sanitize((enabled) -> {
			if (enabled && !idpApplication.findActivationsByUser(entity.getId(), findUserIdentity().getOrganizationId()).isEmpty()) {
				return new SanitizerValidationImpl(Severity.ERROR, "Cannot unlock User with pending Activation");
			}
			
			return new SanitizerValidationImpl(Severity.OK, "");
		}, entity.isEnabled()).getValidations());
		
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
	
		try {
			User user = idpApplication.updateUser(entity.getId(), null, entity.isAdministrator(), entity.isEnabled(),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			entity.setEmail(user.getEmail());
		} catch (UserException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@Override
	public Response deleteUser(UserEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
	
		try {
			idpApplication.deleteUser(entity.getId(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (UserException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(204).build();
	}
	
	@Override
	public Response getApplications() {
		Set<ApplicationEntity> entities = new HashSet<>();
		
		for (Application application : idpApplication.findAllApplications(findUserIdentity().getOrganizationId())) {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId(application.getExternalId());
			entity.setUrn(application.getUrn());
			entity.setName(application.getName());
			entity.setDescription(application.getDescription());
			entity.setAcsUrl(application.getAssertionUrl());
			entity.setLogoutUrl(application.getLogoutUrl());
			
			entities.add(entity);
		}
		
		return Response.ok(entities).build();
	}
	
	@Override
	public Response getApplication(String urn) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(urn);
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		ApplicationEntity entity = new ApplicationEntity();
		
		try {
			Application application = idpApplication.findApplicationByURN(urn, findUserIdentity().getOrganizationId());
			Map<String, Federation> federations = idpApplication.findFederationsByURN(urn, findUserIdentity().getOrganizationId()).stream().collect(Collectors.toMap(fed -> fed.getUser().getExternalId(), fed -> fed));
			
			entity.setId(application.getExternalId());
			entity.setUrn(application.getUrn());
			entity.setName(application.getName());
			entity.setDescription(application.getDescription());
			entity.setAcsUrl(application.getAssertionUrl());
			entity.setLogoutUrl(application.getLogoutUrl());
			
			for (Access access : application.getAccesses()) {
				UserEntity userEntity = new UserEntity();
				userEntity.setId(access.getUser().getExternalId());
				userEntity.setEmail(access.getUser().getEmail());
				
				RoleEntity roleEntity = new RoleEntity();
				roleEntity.setId(access.getRole().getExternalId());
				roleEntity.setName(access.getRole().getName());
				roleEntity.setUri(access.getRole().getUri());
				
				AccessEntity accessEntity = new AccessEntity();
				accessEntity.setId(access.getExternalId());
				accessEntity.setEnabled(access.isEnabled());
				accessEntity.setUser(userEntity);
				accessEntity.setRole(roleEntity);

				FederationEntity federationEntity = new FederationEntity();
				if (federations.get(access.getUser().getExternalId()) != null) {
					federationEntity.setId(federations.get(access.getUser().getExternalId()).getExternalId());
					federationEntity.setEnabled(federations.get(access.getUser().getExternalId()).isEnabled());
				}
				accessEntity.setFederation(federationEntity);
				
				entity.getAccesses().add(accessEntity);
			}
			
			for (Claim claim : application.getClaims()) {
				ClaimEntity claimEntity = new ClaimEntity();
				claimEntity.setId(claim.getExternalId());
				claimEntity.setUri(claim.getUri());
				claimEntity.setName(claim.getName());
				claimEntity.setDescription(claim.getDescription());
				
				entity.getClaims().add(claimEntity);
			}
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.ok(entity).build();
	}
	
	@Override
	public Response createApplication(ApplicationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getUrn(), entity.getName(), entity.getAcsUrl(), entity.getLogoutUrl());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			Application application = idpApplication.createApplication(entity.getUrn(), entity.getName(), entity.getDescription(), entity.getAcsUrl(), entity.getLogoutUrl(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			entity.setId(application.getExternalId());
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(409).entity(exceptionEntity).build();
		} catch (OrganizationException  e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(201).entity(entity).build();
	}
	
	@Override
	public Response updateApplication(ApplicationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId(), entity.getUrn(), entity.getName(), entity.getAcsUrl(), entity.getLogoutUrl());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.updateApplication(entity.getId(), entity.getUrn(), entity.getName(), entity.getDescription(), entity.getLogoutUrl(), entity.getAcsUrl(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException  e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@Override
	public Response updateApplicationClaims(List<ClaimEntity> entities, String id) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(id);
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		Set<String> entitiesName = entities.stream().map(entity -> entity.getName()).collect(Collectors.toSet());
		Application application = null;
		
		try {
			application = idpApplication.updateApplicationClaims(id,
					idpApplication.findAllClaims(findUserIdentity().getOrganizationId()).stream().
							filter(claim -> entitiesName.contains(claim.getName().toLowerCase())).
							collect(Collectors.toSet()),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException  e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		entities = new ArrayList<>();
		for (Claim claim : application.getClaims()) {
			ClaimEntity claimEntity = new ClaimEntity();
			claimEntity.setId(claim.getExternalId());
			claimEntity.setUri(claim.getUri());
			claimEntity.setName(claim.getName());
			claimEntity.setDescription(claim.getDescription());
			
			entities.add(claimEntity);
		}
		
		return Response.status(202).entity(entities).build();
	}
	
	@Override
	public Response deleteApplication(ApplicationEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.deleteApplication(entity.getId(),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException  e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(204).build();
	}
	
	@Override
	public Response createAccess(AccessEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getUser().getId(), entity.getRole().getId(), entity.getApplication().getUrn());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			Access access = idpApplication.createAccess(
					idpApplication.findUserByExternalId(entity.getUser().getId(), findUserIdentity().getOrganizationId()),
					idpApplication.findApplicationByURN(entity.getApplication().getUrn(), findUserIdentity().getOrganizationId()),
					idpApplication.findRoleByExternalId(entity.getRole().getId(), findUserIdentity().getOrganizationId()), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			
			UserEntity userEntity = new UserEntity();
			userEntity.setId(access.getUser().getExternalId());
			userEntity.setEmail(access.getUser().getEmail());
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId(access.getRole().getExternalId());
			roleEntity.setName(access.getRole().getName());
			roleEntity.setUri(access.getRole().getUri());
			
			entity.setId(access.getExternalId());
			entity.setEnabled(access.isEnabled());
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			
			try {
				Federation federation = idpApplication.findFederationByUserAndURN(entity.getUser().getId(), entity.getApplication().getUrn(), findUserIdentity().getOrganizationId());
				
				FederationEntity federationEntity = new FederationEntity();
				federationEntity.setId(federation.getExternalId());
				federationEntity.setEnabled(federation.isEnabled());
				entity.setFederation(federationEntity);
			} catch (FederationException e) {
				entity.setFederation(new FederationEntity());
			}
		} catch (AccessException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(409).entity(exceptionEntity).build();
		} catch (UserException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (ApplicationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (RoleException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(201).entity(entity).build();
	}
	
	@Override
	public Response updateAccess(AccessEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			Access access = idpApplication.updateAccess(entity.getId(), entity.isEnabled(),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			
			UserEntity userEntity = new UserEntity();
			userEntity.setId(access.getUser().getExternalId());
			userEntity.setEmail(access.getUser().getEmail());
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId(access.getRole().getExternalId());
			roleEntity.setName(access.getRole().getName());
			roleEntity.setUri(access.getRole().getUri());
			
			ApplicationEntity appEntity = new ApplicationEntity();
			appEntity.setId(access.getApplication().getExternalId());
			appEntity.setUrn(access.getApplication().getUrn());
			appEntity.setName(access.getApplication().getName());
			appEntity.setDescription(access.getApplication().getDescription());
			appEntity.setAcsUrl(access.getApplication().getAssertionUrl());
			appEntity.setLogoutUrl(access.getApplication().getLogoutUrl());
			
			entity.setId(access.getExternalId());
			entity.setEnabled(access.isEnabled());
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(appEntity);
			
			try {
				Federation federation = idpApplication.findFederationByUserAndURN(entity.getUser().getId(), entity.getApplication().getUrn(), findUserIdentity().getOrganizationId());
				
				FederationEntity federationEntity = new FederationEntity();
				federationEntity.setId(federation.getExternalId());
				federationEntity.setEnabled(federation.isEnabled());
				entity.setFederation(federationEntity);
			} catch (FederationException e) {
				entity.setFederation(new FederationEntity());
			}
		} catch (AccessException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@Override
	public Response deleteAccess(AccessEntity entity) {
		SanitizerValidationResult validationResult = SanitizerUtils.sanitizeNonEmpty(entity.getId());
		if (validationResult.hasError()) {
			return Response.status(417).entity(validationResult.getFirstError()).build();
		}
		
		try {
			idpApplication.deleteAccess(entity.getId(),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (AccessException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (OrganizationException e) {
			ExceptionEntity exceptionEntity = new ExceptionEntity();
			exceptionEntity.setCode("");
			exceptionEntity.setMessage("");
			exceptionEntity.setReason(e.getMessage());
			
			return Response.status(404).entity(exceptionEntity).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
		
		return Response.status(204).build();
	}
	
	@Override
	public Response getRoles() {
		Set<RoleEntity> entities = new HashSet<>();
		
		for (Role role : idpApplication.findAllRoles(findUserIdentity().getOrganizationId())) {
			RoleEntity entity = new RoleEntity();
			entity.setId(role.getExternalId());
			entity.setUri(role.getUri());
			entity.setName(role.getName());
			
			entities.add(entity);
		}
		
		return Response.status(200).entity(entities).build();
	}
	
	private UserIdentity findUserIdentity() {
		UserAuthenticationToken auth = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		return auth.getDetails().getIdentity();
	}
	
	private String generateActivationAccess(HttpServletRequest req, User user, Activation activation) {
		if (activation == null) {
			return null;
		}
		
		var url = new StringBuilder(req.getScheme());
		url.append("://");
		url.append(configuration.getDomain());
		url.append(configuration.getPath());
		url.append("/activate");
		url.append("?");
		url.append(IdentityProviderConstants.ACTIVATION_PARAM_ACTION);
		url.append("=");
		url.append(IdentityProviderConstants.ACTIVATION_CONST_PASSWORD);
		url.append("&");
		url.append(IdentityProviderConstants.ACTIVATION_PARAM_CODE);
		url.append("=");
		url.append(activation.getExternalId());
		
		return url.toString();
	}

}
