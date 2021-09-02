package com.tazouxme.idp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;
import com.tazouxme.idp.service.entity.AccessEntity;
import com.tazouxme.idp.service.entity.ApplicationEntity;
import com.tazouxme.idp.service.entity.RoleEntity;
import com.tazouxme.idp.service.entity.UserEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;

public class AccessServiceTest extends AbstractServiceTest {
	
	private static final String ACCESS_API = "/access";
	
	@Test
	@Order(1)
	public void createAccessTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(201, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			AccessEntity pEntity = new ObjectMapper().readValue(responseText, AccessEntity.class);
			assertEquals("USE_user2", pEntity.getUser().getId());
			assertEquals("ROL_user", pEntity.getRole().getId());
			assertEquals("urn:com:tazouxme:test1", pEntity.getApplication().getUrn());
			assertFalse(StringUtils.isBlank(pEntity.getFederation().getId()));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(2)
	public void createAccessEmptyUserTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(3)
	public void createAccessEmptyRoleTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(4)
	public void createAccessEmptyApplicationTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(5)
	public void createAccessEmptyUserIdTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(6)
	public void createAccessEmptyRoleIdTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(7)
	public void createAccessEmptyApplicationUrnTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(8)
	public void createAccessWrongUserIdTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("xxx");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(404, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.USER_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(9)
	public void createAccessWrongRoleIdTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("xxx");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(404, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.ROLE_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(10)
	public void createAccessWrongApplicationUrnTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("xxx");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(404, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(11)
	public void createAccessAlreadyExistsTest() {
		try {
			UserEntity userEntity = new UserEntity();
			userEntity.setId("USE_user2");
			
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId("ROL_user");
			
			ApplicationEntity applicationEntity = new ApplicationEntity();
			applicationEntity.setUrn("urn:com:tazouxme:test1");
			
			AccessEntity entity = new AccessEntity();
			entity.setUser(userEntity);
			entity.setRole(roleEntity);
			entity.setApplication(applicationEntity);
			
			String responseText = post(409, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.ACCESS_ALREADY_EXISTS.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(12)
	public void createEmptyAccessTest() {
		try {
			String responseText = post(405, ACCESS_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(13)
	public void updateAccessTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("ACC_2");
			entity.setEnabled(false);
			
			String responseText = patch(202, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			AccessEntity pEntity = new ObjectMapper().readValue(responseText, AccessEntity.class);
			assertEquals("USE_user1", pEntity.getUser().getId());
			assertEquals("ROL_admin", pEntity.getRole().getId());
			assertEquals("urn:com:tazouxme:test2", pEntity.getApplication().getUrn());
			assertFalse(entity.isEnabled());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(14)
	public void updateAccessEmptyIdTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("");
			entity.setEnabled(false);
			
			String responseText = patch(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(15)
	public void updateAccessWrongIdTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("ACC_X");
			entity.setEnabled(false);
			
			String responseText = patch(404, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.ACCESS_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(16)
	public void updateEmptyAccessTest() {
		try {
			String responseText = patch(405, ACCESS_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(17)
	public void deleteAccessTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("ACC_2");
			entity.setEnabled(false);
			
			delete(204, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(18)
	public void deleteAccessEmptyIdTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("");
			entity.setEnabled(false);
			
			String responseText = delete(417, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(19)
	public void deleteAccessWrongIdTest() {
		try {
			AccessEntity entity = new AccessEntity();
			entity.setId("ACC_X");
			entity.setEnabled(false);
			
			String responseText = delete(404, ACCESS_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.ACCESS_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(20)
	public void deleteEmptyAccessEmptyIdTest() {
		try {
			String responseText = delete(405, ACCESS_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}

}
