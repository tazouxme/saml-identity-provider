package com.tazouxme.idp.service.contract;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;
import com.tazouxme.idp.service.entity.AccessEntity;
import com.tazouxme.idp.service.entity.ApplicationEntity;
import com.tazouxme.idp.service.entity.ClaimEntity;
import com.tazouxme.idp.service.entity.OrganizationEntity;
import com.tazouxme.idp.service.entity.UserEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/v1")
public interface IIdentityProviderService {
	
	@GET
	@Path("/organization")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get the Organization", 
		description = "Get all information about the Organization", 
		responses = {
			@ApiResponse(description = "Found Organization entity", content = @Content(schema = @Schema(implementation = OrganizationEntity.class)), responseCode = "200"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response getOrganization();

	@PATCH
	@Path("/organization")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Update the Organization", 
		description = "Update the information of the Organization", 
		responses = {
			@ApiResponse(description = "Updated Organization entity", content = @Content(schema = @Schema(implementation = OrganizationEntity.class)), responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' or 'name' from the entity is not set or wrong ID passed to the entity", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response updateOrganization(@Parameter(required = true) OrganizationEntity entity);

	@PUT
	@Path("/certificate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Set a Certificate", 
		description = "Set a Certificate for the Organization", 
		responses = {
			@ApiResponse(description = "Updated Organization entity", content = @Content(schema = @Schema(implementation = OrganizationEntity.class)), responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'certificate' from the entity is not set or wrong ID passed to the entity", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")	
		}
	)
	public Response setCertificate(@Parameter(required = true) OrganizationEntity entity);

	@DELETE
	@Path("/certificate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Delete a Certificate", 
		description = "Delete the Certificate for the Organization",
		responses = {
			@ApiResponse(description = "Certificate successfully deleted", responseCode = "204"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'certificate' from the entity is set or wrong ID passed to the entity", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")	
		}
	)
	public Response deleteCertificate(@Parameter(required = true) OrganizationEntity entity);

	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all Users", 
		description = "Get all users from the Organization",
		responses = {
			@ApiResponse(description = "Found User entities", content = @Content(array =  @ArraySchema(arraySchema = @Schema(implementation = UserEntity.class))), responseCode = "200"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")		
		}
	)
	public Response getUsers();

	@GET
	@Path("/user/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get a User by ID", 
		description = "Get a user from the Organization using the External ID",
		responses = {
			@ApiResponse(description = "Found User entity", content = @Content(schema = @Schema(implementation = UserEntity.class)), responseCode = "200"),
			@ApiResponse(description = "Unable to get the User", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")	
		}
	)
	public Response getUser(@Parameter(required = true) @PathParam("id") String id);

	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Create a User", 
		description = "Create a new user into the Organization",
		responses = {
			@ApiResponse(description = "Created User entity", responseCode = "201"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Activation link cannot be created", content = @Content(schema = @Schema(implementation = SanitizerValidationImpl.class)), responseCode = "406"),
			@ApiResponse(description = "User already exists", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "409"),
			@ApiResponse(description = "Variable 'username' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response createUser(@Context HttpServletRequest request, @Parameter(required = true) UserEntity entity);

	@PATCH
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Update a User", 
		description = "Update an existing user into the Organization",
		responses = {
			@ApiResponse(description = "Updated User entity", responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization or User", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' from the entity is not set or an activation is pending", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response updateUser(@Parameter(required = true) UserEntity entity);

	@DELETE
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Delete a User", 
		description = "Delete an existing user from the Organization",
		responses = {
			@ApiResponse(description = "Deleted User entity", responseCode = "204"),
			@ApiResponse(description = "Unable to get the Organization or User", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response deleteUser(@Parameter(required = true) UserEntity entity);

	@GET
	@Path("/applications")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all Applications", 
		description = "Get all applications from the Organization",
		responses = {
			@ApiResponse(description = "All found Applications", responseCode = "200")
		}
	)
	public Response getApplications();

	@GET
	@Path("/application/{urn}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get an Application by URN", 
		description = "Get an existing application from the Organization using the URN",
		responses = {
			@ApiResponse(description = "Found Application", responseCode = "200"),
			@ApiResponse(description = "Unable to get the Application", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'urn' in URI is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response getApplication(@Parameter(required = true) @PathParam("urn") String urn);

	@POST
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Create an Application", 
		description = "Create a new application into the Organization",
		responses = {
			@ApiResponse(description = "Created Application entity", responseCode = "201"),
			@ApiResponse(description = "Unable to get the Organization", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Application already exists", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "409"),
			@ApiResponse(description = "Variable 'urn', 'name', 'acsUrl' or 'logoutUrl' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response createApplication(@Parameter(required = true) ApplicationEntity entity);

	@PATCH
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Update an Application", 
		description = "Update an existing application from the Organization",
		responses = {
			@ApiResponse(description = "Updated Application entity", responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization or Application", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'urn', 'name', 'acsUrl' or 'logoutUrl' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response updateApplication(@Parameter(required = true) ApplicationEntity entity);

	@PATCH
	@Path("/application/{id}/claims")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Update all Application's Claims", 
		description = "Update all claims for an existing application from the Organization",
		responses = {
			@ApiResponse(description = "Updated Application entity", responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization or Application", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' in URI is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response updateApplicationClaims(@Parameter(required = true) List<ClaimEntity> entities, @Parameter(required = true) @PathParam("id") String id);

	@DELETE
	@Path("/application")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Delete an Application", 
		description = "Delete an existing application from the Organization",
		responses = {
			@ApiResponse(description = "Application entity deleted", responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization or Application", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response deleteApplication(@Parameter(required = true) ApplicationEntity entity);

	@POST
	@Path("/access")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Create an Access", 
		description = "Create a new access into the Organization",
		responses = {
			@ApiResponse(description = "Created Access entity", responseCode = "201"),
			@ApiResponse(description = "Unable to get the Organization or Role or Application or User", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Access already exists", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "409"),
			@ApiResponse(description = "Variable User 'id', Role 'id' or Application 'urn' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response createAccess(@Parameter(required = true) AccessEntity entity);

	@PATCH
	@Path("/access")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Update an Access", 
		description = "Update an existing access from the Organization",
		responses = {
			@ApiResponse(description = "Updated Access entity", responseCode = "202"),
			@ApiResponse(description = "Unable to get the Organization or Access", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' in entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response updateAccess(@Parameter(required = true) AccessEntity entity);

	@DELETE
	@Path("/access")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Delete an Access", 
		description = "Delete an existing access from the Organization",
		responses = {
			@ApiResponse(description = "Access entity deleted", responseCode = "204"),
			@ApiResponse(description = "Unable to get the Organization or Access", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "404"),
			@ApiResponse(description = "Variable 'id' from the entity is not set", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "417"),
			@ApiResponse(description = "Unexpected server error", content = @Content(schema = @Schema(implementation = ExceptionEntity.class)), responseCode = "500")
		}
	)
	public Response deleteAccess(@Parameter(required = true) AccessEntity entity);

	@GET
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all Roles", 
		description = "Get all roles from the Organization",
		responses = {
			@ApiResponse(description = "All found Roles", responseCode = "200")
		}
	)
	public Response getRoles();

}
