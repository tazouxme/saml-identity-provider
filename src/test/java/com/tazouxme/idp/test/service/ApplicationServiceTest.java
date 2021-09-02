package com.tazouxme.idp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;
import com.tazouxme.idp.service.entity.ApplicationEntity;
import com.tazouxme.idp.service.entity.ClaimEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;

public class ApplicationServiceTest extends AbstractServiceTest {
	
	private static final String APPLICATIONS_API = "/applications";
	private static final String APPLICATION_API = "/application";
	private static final String CLAIMS_API = "/claims";
	
	@Test
	@Order(1)
	public void getApplicationsTest() {
		try {
			String responseText = get(200, APPLICATIONS_API);
			
			@SuppressWarnings("unchecked")
			Set<ApplicationEntity> entities = new ObjectMapper().readValue(responseText, Set.class);
			assertEquals(2, entities.size());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(2)
	public void getApplicationTest() {
		try {
			String responseText = get(200, APPLICATION_API + "/urn:com:tazouxme:test1");
			
			ApplicationEntity entity = new ObjectMapper().readValue(responseText, ApplicationEntity.class);
			assertEquals("APP_test1", entity.getId());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(3)
	public void getApplicationWrongUrnTest() {
		try {
			String responseText = get(404, APPLICATION_API + "/urn:com:tazouxme:testx");
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND.getCode(), entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(4)
	public void createApplicationTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("urn:com:tazouxme:test3");
			entity.setName("Test 3");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = post(201, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ApplicationEntity pEntity = new ObjectMapper().readValue(responseText, ApplicationEntity.class);
			assertEquals("urn:com:tazouxme:test3", pEntity.getUrn());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(5)
	public void createApplicationEmptyUrnTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("");
			entity.setName("Test 3");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = post(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(6)
	public void createApplicationEmptyNameTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("xxx");
			entity.setName("");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = post(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(7)
	public void createApplicationEmptyAcsUrlTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("xxx");
			entity.setName("Test 3");
			entity.setAcsUrl("");
			entity.setLogoutUrl("/logout");
			
			String responseText = post(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(8)
	public void createApplicationEmptyLogoutUrlTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("xxx");
			entity.setName("Test 3");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("");
			
			String responseText = post(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(9)
	public void createApplicationAlreadyExistsTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setUrn("urn:com:tazouxme:test3");
			entity.setName("Test 3");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = post(409, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_ALREADY_EXISTS.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(10)
	public void createEmptyApplicationTest() {
		try {
			String responseText = post(405, APPLICATION_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(11)
	public void updateApplicationTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("Test 2");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(202, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ApplicationEntity pEntity = new ObjectMapper().readValue(responseText, ApplicationEntity.class);
			assertEquals("urn:com:tazouxme:test2", pEntity.getUrn());
			assertEquals("/acs", pEntity.getAcsUrl());
			assertEquals("/logout", pEntity.getLogoutUrl());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(12)
	public void updateApplicationEmptyIdTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("Test 2");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(13)
	public void updateApplicationEmptyUrnTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			entity.setUrn("");
			entity.setName("Test 2");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(14)
	public void updateApplicationEmptyNameTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(15)
	public void updateApplicationEmptyAcsUrlTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("Test 2");
			entity.setAcsUrl("");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(16)
	public void updateApplicationEmptyLogoutUrlTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("Test 2");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("");
			
			String responseText = patch(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(17)
	public void updateApplicationWrongIdTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_testX");
			entity.setUrn("urn:com:tazouxme:test2");
			entity.setName("Test 2");
			entity.setAcsUrl("/acs");
			entity.setLogoutUrl("/logout");
			
			String responseText = patch(404, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(18)
	public void updateEmptyApplicationTest() {
		try {
			String responseText = patch(405, APPLICATION_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(19)
	public void updateApplicationClaimsTest() {
		try {
			ClaimEntity entity = new ClaimEntity();
			entity.setName("ORG");
			
			List<ClaimEntity> entities = new ArrayList<>();
			entities.add(entity);
			
			String responseText = patch(202, APPLICATION_API + "/APP_test2/claims", new ObjectMapper().writeValueAsString(entities));
			
			@SuppressWarnings("unchecked")
			List<ClaimEntity> pEntities = new ObjectMapper().readValue(responseText, List.class);
			assertEquals(1, pEntities.size());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(20)
	public void updateApplicationClaimsWrongIdTest() {
		try {
			ClaimEntity entity = new ClaimEntity();
			entity.setName("ORG");
			
			List<ClaimEntity> entities = new ArrayList<>();
			entities.add(entity);
			
			String responseText = patch(404, APPLICATION_API + "/APP_testX" + CLAIMS_API, new ObjectMapper().writeValueAsString(entities));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(21)
	public void updateEmptyApplicationClaimsTest() {
		try {
			String responseText = patch(405, APPLICATION_API + "/APP_test2/claims", null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(22)
	public void deleteApplicationTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_test2");
			
			delete(204, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(23)
	public void deleteApplicationEmptyIdTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("");
			
			String responseText = delete(417, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(24)
	public void deleteApplicationWrongIdTest() {
		try {
			ApplicationEntity entity = new ApplicationEntity();
			entity.setId("APP_testX");
			
			String responseText = delete(404, APPLICATION_API, new ObjectMapper().writeValueAsString(entity));
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals(IdentityProviderExceptionCode.APPLICATION_NOT_FOUND.getCode(), pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(25)
	public void deleteEmptyApplicationTest() {
		try {
			String responseText = delete(405, APPLICATION_API, null);
			
			ExceptionEntity pEntity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", pEntity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}

}
