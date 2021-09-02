package com.tazouxme.idp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tazouxme.idp.service.entity.OrganizationEntity;
import com.tazouxme.idp.service.entity.exception.ExceptionEntity;

public class OrganizationServiceTest extends AbstractServiceTest {
	
	private static final String ORGANIZATION_API = "/organization";
	private static final String CERTIFICATE_API = "/certificate";
	
	@Test
	@Order(1)
	public void getOrganizationTest() {
		try {
			String responseText = get(200, ORGANIZATION_API);
			
			OrganizationEntity entity = new ObjectMapper().readValue(responseText, OrganizationEntity.class);
			assertEquals("ORG_test", entity.getId());
			assertEquals("Test", entity.getName());
			assertTrue(StringUtils.isBlank(entity.getCertificate()));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(2)
	public void patchOrganizationTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setName("New Test");
			
			String responseText = patch(202, ORGANIZATION_API, new ObjectMapper().writeValueAsString(organization));
			
			OrganizationEntity entity = new ObjectMapper().readValue(responseText, OrganizationEntity.class);
			assertEquals("ORG_test", entity.getId());
			assertEquals("New Test", entity.getName());
			assertTrue(StringUtils.isBlank(entity.getCertificate()));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(3)
	public void patchOrganizationBlankIdTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("");
			organization.setName("New Test");
			
			String responseText = patch(417, ORGANIZATION_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(4)
	public void patchOrganizationBlankNameTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setName("");
			
			String responseText = patch(417, ORGANIZATION_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(5)
	public void patchEmptyOrganizationTest() {
		try {
			String responseText = patch(405, ORGANIZATION_API, null);
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(6)
	public void setCertificateTest() {
		Resource resource = resourceLoader.getResource("classpath:/cert/tz-idp.crt");
		
		try {
			String s = extractInputStream(resource.getInputStream()) + "\r\n";
			String cert = new String(Base64.encode(s.getBytes()));
			
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setCertificate(cert);
			
			String responseText = put(202, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
			
			OrganizationEntity entity = new ObjectMapper().readValue(responseText, OrganizationEntity.class);
			assertEquals("ORG_test", entity.getId());
			assertTrue(StringUtils.isBlank(entity.getCertificate()));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(7)
	public void setCertificateMalformedTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setCertificate("xxx");
			
			String responseText = put(417, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(8)
	public void setCertificateWrongIdTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("xxx");
			organization.setCertificate("xxx");
			
			String responseText = put(417, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(9)
	public void setEmptyCertificateTest() {
		try {
			String responseText = put(405, CERTIFICATE_API, null);
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(10)
	public void deleteCertificateTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setCertificate("");
			
			delete(204, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(11)
	public void deleteCertificateNonBlankTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("ORG_test");
			organization.setCertificate("x");
			
			String responseText = delete(417, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(12)
	public void deleteCertificateWrongIdTest() {
		try {
			OrganizationEntity organization = new OrganizationEntity();
			organization.setId("xxx");
			organization.setCertificate("");
			
			String responseText = delete(417, CERTIFICATE_API, new ObjectMapper().writeValueAsString(organization));
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}
	
	@Test
	@Order(13)
	public void deleteEmptyCertificateTest() {
		try {
			String responseText = delete(405, CERTIFICATE_API, null);
			
			ExceptionEntity entity = new ObjectMapper().readValue(responseText, ExceptionEntity.class);
			assertEquals("VALIDATION_FAILED", entity.getCode());
		} catch (Exception e) {
			fail(e);
		}
	}

}
