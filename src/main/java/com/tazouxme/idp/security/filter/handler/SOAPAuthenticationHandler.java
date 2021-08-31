package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.binding.artifact.impl.StorageServiceSAMLArtifactMap;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.navigate.SAMLMessageContextIssuerFunction;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPSOAP11Encoder;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.profile.impl.ResolveArtifact;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.storage.IdpStorageService;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.util.SAMLUtils;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class SOAPAuthenticationHandler extends AbstractAuthenticationHandler {
	
	public SOAPAuthenticationHandler(ApplicationContext context) {
		super(context);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		if (parameters.getSoapRequest() instanceof ArtifactResolve) {
			ArtifactResolve artifactResolve = (ArtifactResolve) parameters.getSoapRequest();
			
			if (artifactResolve == null) {
				fault(request, response, authentication);
				return;
			}
			
			handleArtifact(request, response, authentication, artifactResolve);
		} else if (parameters.getSoapRequest() instanceof AttributeQuery) {
			AttributeQuery attributeQuery = (AttributeQuery) parameters.getSoapRequest();
			
			if (attributeQuery == null) {
				fault(request, response, authentication);
				return;
			}
			
			handleAttribute(request, response, authentication, attributeQuery);
		} else {
			
		}
	}
	
	private void handleArtifact(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication, ArtifactResolve artifactResolve) throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		ArtifactResponse artifactResponse = SAMLUtils.buildArtifactResponse(parameters, authentication.getDetails().getResultCode());
		
        try {
	        MessageContext contextIn = new MessageContext();
	        contextIn.setMessage(artifactResolve);
	        contextIn.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId(artifactResolve.getIssuer().getValue());
	        
	        MessageContext contextOut = new MessageContext();
			contextOut.setMessage(artifactResponse);
			contextOut.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId(getConfiguration().getUrn());
	        
	        ProfileRequestContext ctx = new ProfileRequestContext();
	        ctx.setInboundMessageContext(contextIn);
	        ctx.setOutboundMessageContext(contextOut);
	        
	        StorageServiceSAMLArtifactMap storgeService = new StorageServiceSAMLArtifactMap();
			storgeService.setStorageService(new IdpStorageService(getContext()));
			storgeService.initialize();
			
			ResolveArtifact resolver = new ResolveArtifact();
			resolver.setArtifactMap(storgeService);
			resolver.setIssuerLookupStrategy(new SAMLMessageContextIssuerFunction().compose(new OutboundMessageContextLookup()));
			resolver.execute(ctx);
			
			MessageContext context = new MessageContext();
			context.setMessage(artifactResponse);

			HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
			encoder.setMessageContext(context);
			encoder.setHttpServletResponse(response);

			try {
				encoder.prepareContext();
				encoder.initialize();
				encoder.encode();
			} catch (MessageEncodingException e) {
				fault(request, response, authentication);
			} catch (ComponentInitializationException e) {
				fault(request, response, authentication);
			}
        } catch (ComponentInitializationException e) {
			fault(request, response, authentication);
        }
		
		SecurityContextHolder.clearContext();
	}
	
	private void handleAttribute(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication, AttributeQuery attributeQuery) throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		Set<String> attributes = attributeQuery.getAttributes().stream().map(Attribute::getName).collect(Collectors.toSet());
		
		Set<Claim> claims = parameters.getApplication().getClaims();
		Set<UserDetails> details = parameters.getUser().getDetails();
		
		if (!claims.stream().map(Claim::getUri).collect(Collectors.toSet()).containsAll(attributes)) {
			// not allowed
			fault(request, response, authentication);
			return;
		}
		
		for (UserDetails detail : details) {
			if (attributes.contains(detail.getClaim().getUri())) {
				authentication.getDetails().getIdentity().getClaims().put(detail.getClaim().getUri(), detail.getClaimValue());
			}
		}
		
        MessageContext context = new MessageContext();
		context.setMessage(SAMLUtils.buildAttributeQueryResponse(parameters, authentication.getDetails().getIdentity(), authentication.getDetails().getResultCode()));

		HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
		encoder.setMessageContext(context);
		encoder.setHttpServletResponse(response);

		try {
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e) {
			fault(request, response, authentication);
		} catch (ComponentInitializationException e) {
			fault(request, response, authentication);
		}
		
		SecurityContextHolder.clearContext();
	}
	
	@Override
	public void fault(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		MessageContext context = new MessageContext();
		context.setMessage("");

		HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
		encoder.setMessageContext(context);
		encoder.setHttpServletResponse(response);

		try {
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1401);
		} catch (ComponentInitializationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1402);
		}
	}

}
