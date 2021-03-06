package com.tazouxme.idp.security.filter.handler;

import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXMLMessageEncoder;
import org.opensaml.saml.common.binding.artifact.impl.StorageServiceSAMLArtifactMap;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPArtifactEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostSimpleSignEncoder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.storage.IdpStorageService;
import com.tazouxme.idp.security.velocity.IdpVelocityEngine;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class SAMLAuthenticationEncoderFactory {
	
	public static BaseHttpServletResponseXMLMessageEncoder getEncoder(boolean success, AuthnRequest request, ApplicationContext applicationContext) throws ComponentInitializationException {
		IdentityProviderConfiguration configuration = applicationContext.getBean(IdentityProviderConfiguration.class);
		
		if (success) {
			if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(request.getProtocolBinding()) ||
					SAMLConstants.SAML2_POST_BINDING_URI.equals(request.getProtocolBinding())) {
				HTTPPostEncoder encoder = new HTTPPostEncoder();
				encoder.setVelocityEngine(new IdpVelocityEngine());
				encoder.setVelocityTemplateId(configuration.getTemplates().getPostTemplate());
				
				return encoder;
			}
			
			if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(request.getProtocolBinding())) {
				HTTPPostSimpleSignEncoder encoder = new HTTPPostSimpleSignEncoder();
				encoder.setVelocityEngine(new IdpVelocityEngine());
				encoder.setVelocityTemplateId(configuration.getTemplates().getPostSimpleSignTemplate());
				
				return encoder;
			}
			
			if (SAMLConstants.SAML2_ARTIFACT_BINDING_URI.equals(request.getProtocolBinding())) {
				HTTPArtifactEncoder encoder = new HTTPArtifactEncoder();
				encoder.setPostEncoding(true);
				encoder.setVelocityEngine(new IdpVelocityEngine());
				encoder.setVelocityTemplateId(configuration.getTemplates().getArtifactTemplate());
				
				StorageServiceSAMLArtifactMap storgeService = new StorageServiceSAMLArtifactMap();
				storgeService.setStorageService(new IdpStorageService(applicationContext));
				storgeService.initialize();
				encoder.setArtifactMap(storgeService);
				
				return encoder;
			}
		} else {
			if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(request.getProtocolBinding())) {
				HTTPPostSimpleSignEncoder encoder = new HTTPPostSimpleSignEncoder();
				encoder.setVelocityEngine(new IdpVelocityEngine());
				encoder.setVelocityTemplateId(configuration.getTemplates().getPostSimpleSignTemplate());
				
				return encoder;
			}
			
			HTTPPostEncoder encoder = new HTTPPostEncoder();
			encoder.setVelocityEngine(new IdpVelocityEngine());
			encoder.setVelocityTemplateId(configuration.getTemplates().getPostTemplate());
			
			return encoder;
		}
		
		return null;
	}

}
