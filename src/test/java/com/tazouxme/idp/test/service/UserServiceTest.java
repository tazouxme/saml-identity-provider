package com.tazouxme.idp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Set;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;
import com.tazouxme.idp.service.entity.UserEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;

public class UserServiceTest extends AbstractServiceTest {
	
	private static final String USERS_API = "/users";
	private static final String USER_API = "/user";
	
	@Test
	@Order(1)
	public void getUsersTest() {
		try {
			String responseText = get(200, USERS_API);
			
			@SuppressWarnings("unchecked")
			Set<UserEntity> entities = new ObjectMapper().readValue(responseText, Set.class);
			assertEquals(3, entities.size());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(2)
	public void getUserTest() {
		try {
			String responseText = get(200, USER_API + "/USE_user1");
			
			UserEntity entity = new ObjectMapper().readValue(responseText, UserEntity.class);
			assertEquals("USE_user1", entity.getId());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(3)
	public void getUserWrongIdTest() {
		try {
			String responseText = get(404, USER_API + "/USE_userX");
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.USER_NOT_FOUND.getCode(), entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(4)
	public void createUserTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setUsername("user.name");
			entity.setAdministrator(false);
			
			String responseText = post(201, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			UserEntity pEntity = new ObjectMapper().readValue(responseText, UserEntity.class);
			assertEquals("user.name", pEntity.getUsername());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(5)
	public void createUserWrongUsernameTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setUsername("");
			entity.setAdministrator(false);
			
			String responseText = post(417, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(6)
	public void createUserAlreadyExistsTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setUsername("user.name");
			entity.setAdministrator(false);
			
			String responseText = post(409, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.USER_ALREADY_EXISTS.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(7)
	public void createEmptyUserTest() {
		try {
			String responseText = post(405, USER_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(8)
	public void updateUserTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("USE_user2");
			entity.setAdministrator(true);
			
			String responseText = patch(202, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			UserEntity pEntity = new ObjectMapper().readValue(responseText, UserEntity.class);
			assertEquals("USE_user2", pEntity.getId());
			assertEquals(true, pEntity.isAdministrator());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(9)
	public void updateUserStillInactiveTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("USE_user3");
			entity.setEnabled(true);
			
			String responseText = patch(417, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(10)
	public void updateUserEmptyIdTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("");
			entity.setAdministrator(true);
			
			String responseText = patch(417, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(11)
	public void updateUserWrongIdTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("USE_userX");
			entity.setAdministrator(true);
			
			String responseText = patch(404, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.USER_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(12)
	public void updateEmptyUserTest() {
		try {
			String responseText = patch(405, USER_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(13)
	public void deleteUserTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("USE_user3");
			entity.setAdministrator(true);
			
			delete(204, USER_API, new ObjectMapper().writeValueAsString(entity));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(14)
	public void deleteUserEmptyIdTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("");
			entity.setAdministrator(true);
			
			String responseText = delete(417, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(15)
	public void deleteUserWrongIdTest() {
		try {
			UserEntity entity = new UserEntity();
			entity.setId("USE_userX");
			entity.setAdministrator(true);
			
			String responseText = delete(404, USER_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.USER_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(16)
	public void deleteEmptyUserTest() {
		try {
			String responseText = delete(405, USER_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}

}
