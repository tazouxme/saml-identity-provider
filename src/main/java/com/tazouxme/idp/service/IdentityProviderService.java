package com.tazouxme.idp.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.Role;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserIdentity;
import com.tazouxme.idp.service.entity.AccessEntity;
import com.tazouxme.idp.service.entity.ApplicationEntity;
import com.tazouxme.idp.service.entity.ClaimEntity;
import com.tazouxme.idp.service.entity.OrganizationEntity;
import com.tazouxme.idp.service.entity.RoleEntity;
import com.tazouxme.idp.service.entity.UserDetailsEntity;
import com.tazouxme.idp.service.entity.UserEntity;

public class IdentityProviderService {
	
	@Autowired
	private IIdentityProviderApplication idpApplication;
	
	@GET
	@Path("/organization")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization() {
		try {
			Organization organization = idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId());
			
			OrganizationEntity entity = new OrganizationEntity();
			entity.setId(organization.getExternalId());
			entity.setCode(organization.getCode());
			entity.setDomain(organization.getDomain());
			entity.setName(organization.getName());
			entity.setDescription(organization.getDescription());
			entity.setPublicKey(organization.getPublicKey());
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
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
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
			return Response.status(500).build();
		}
		
		return Response.ok(entities).build();
	}
	
	@GET
	@Path("/user/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("id") String id) {
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
			return Response.status(500).build();
		}
		
		return Response.ok(entity).build();
	}
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(UserEntity entity) {
		try {
			User user = idpApplication.createUser(entity.getUsername(), "changeit", entity.isAdministrator(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			
			idpApplication.createActivation(user.getOrganization().getExternalId(), user.getExternalId(), IdentityProviderConstants.ACTIVATION_CONST_PASSWORD);
			
			entity.setId(user.getExternalId());
			entity.setEmail(user.getEmail());
			entity.setEnabled(user.isEnabled());
		} catch (UserException | OrganizationException | ActivationException e) {
			return Response.status(500).build();
		}
		
		return Response.status(201).entity(entity).build();
	}
	
	@PATCH
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUser(UserEntity entity) {
		return Response.status(202).entity(entity).build();
	}
	
	@DELETE
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUser(UserEntity entity) {
		return Response.status(204).build();
	}
	
	@GET
	@Path("/applications")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplications() {
		Set<ApplicationEntity> entities = new HashSet<>();
		
		for (Application application : idpApplication.findAllApplications(findUserIdentity().getOrganizationId())) {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId(application.getExternalId());
			entity.setUrn(application.getUrn());
			entity.setName(application.getName());
			entity.setDescription(application.getDescription());
			entity.setAcsUrl(application.getAssertionUrl());
			
			entities.add(entity);
		}
		
		return Response.ok(entities).build();
	}
	
	@GET
	@Path("/application/{urn}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApplication(@PathParam("urn") String urn) {
		ApplicationEntity entity = new ApplicationEntity();
		
		try {
			Application application = idpApplication.findApplicationByURN(urn, findUserIdentity().getOrganizationId());
			entity.setId(application.getExternalId());
			entity.setUrn(application.getUrn());
			entity.setName(application.getName());
			entity.setDescription(application.getDescription());
			entity.setAcsUrl(application.getAssertionUrl());
			
			for (Access access : application.getAccesses()) {
				AccessEntity accessEntity = new AccessEntity();
				accessEntity.setId(access.getExternalId());
				accessEntity.setEmail(access.getUser().getEmail());
				accessEntity.setRole(access.getRole().getName());
				accessEntity.setEnabled(access.isEnabled());
				
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
			return Response.status(500).build();
		}
		
		return Response.ok(entity).build();
	}
	
	@POST
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(ApplicationEntity entity) {
		try {
			Application application = idpApplication.createApplication(entity.getUrn(), entity.getName(), entity.getDescription(), entity.getAcsUrl(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
			entity.setId(application.getExternalId());
		} catch (OrganizationException e) {
			return Response.status(500).build();
		} catch (ApplicationException e) {
			return Response.status(500).build();
		}
		
		return Response.status(201).entity(entity).build();
	}
	
	@PATCH
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(ApplicationEntity entity) {
		try {
			idpApplication.updateApplication(entity.getId(), entity.getUrn(), entity.getName(), entity.getDescription(), entity.getAcsUrl(), 
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (OrganizationException e) {
			return Response.status(500).build();
		} catch (ApplicationException e) {
			return Response.status(500).build();
		}
		
		return Response.status(202).entity(entity).build();
	}
	
	@DELETE
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteApplication(ApplicationEntity entity) {
		try {
			idpApplication.deleteApplication(entity.getId(),
					idpApplication.findOrganizationByExternalId(findUserIdentity().getOrganizationId()));
		} catch (OrganizationException e) {
			return Response.status(500).build();
		} catch (ApplicationException e) {
			return Response.status(500).build();
		}
		
		return Response.status(204).build();
	}
	
	private UserIdentity findUserIdentity() {
		UserAuthenticationToken auth = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		return auth.getDetails().getIdentity();
	}

}
