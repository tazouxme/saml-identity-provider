package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXMLMessageEncoder;
import org.opensaml.saml.common.messaging.context.SAMLArtifactContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.util.SAMLUtils;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class SAMLAuthenticationHandler extends AbstractAuthenticationHandler {
	
	public SAMLAuthenticationHandler(ApplicationContext context) {
		super(context);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		AuthnRequest authnRequest = parameters.getAuthnRequest();
		
		if (authnRequest == null) {
			StageResultCode resultCode = authentication.getDetails().getResultCode();
			response.sendError(resultCode.getCode(), resultCode.toString());
			return;
		}
		
		if (UserAuthenticationPhase.MUST_AUTHENTICATE.equals(authentication.getDetails().getPhase())) {
			// Handle Unsecured User / Password Authentication
			if (AuthnContext.PASSWORD_AUTHN_CTX.equals(
					authnRequest.getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
				response.sendRedirect("./login");
				return;
			}
			
			// Handle Secured User / Password Authentication
			else if ("https".equals(request.getScheme()) && AuthnContext.PPT_AUTHN_CTX.equals(
					authnRequest.getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
				response.sendRedirect("./login");
				return;
			}
			
			authentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			authentication.getDetails().setResultCode(StageResultCode.FAT_1301);
		}
		
		if (UserAuthenticationPhase.MUST_ACTIVATE.equals(authentication.getDetails().getPhase())) {
			authentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			authentication.getDetails().setResultCode(StageResultCode.FAT_1305);
		}
		
		boolean success = !UserAuthenticationPhase.SSO_FAILED.equals(authentication.getDetails().getPhase());
		if (success) {
			parameters.setSecretKey(generateSecretKey());
			
			Set<Claim> claims = parameters.getApplication().getClaims();
			Set<UserDetails> details = parameters.getUser().getDetails();
			
			for (UserDetails detail : details) {
				if (claims.contains(detail.getClaim())) {
					authentication.getDetails().getIdentity().getClaims().put(detail.getClaim().getUri(), detail.getClaimValue());
				}
			}
			
			if (!isSuccessfullyLoggedIn(request, response, authentication)) {
				return;
			}
		}
		
		// handle status
		Response samlResponse = SAMLUtils.buildResponse(
				authentication.getDetails().getParameters(),
				authentication.getDetails().getIdentity(),
				authentication.getRole(),
				authentication.getDetails().getResultCode());
		
		MessageContext messageContext = new MessageContext();
		messageContext.setMessage(samlResponse);

		messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(parameters.getRelayStateParam());
		messageContext.getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(SAMLEndpointContext.class, true).setEndpoint(SAMLUtils.getEndpoint(authnRequest.getAssertionConsumerServiceURL()));
		messageContext.getSubcontext(SAMLPeerEntityContext.class, true).setEntityId(authnRequest.getIssuer().getValue());
		messageContext.getSubcontext(SAMLSelfEntityContext.class, true).setEntityId(samlResponse.getIssuer().getValue());
		messageContext.getSubcontext(SAMLArtifactContext.class, true).setSourceArtifactResolutionServiceEndpointIndex(0);
		
		if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(authnRequest.getProtocolBinding())) {
			SignatureSigningParameters signatureSigningParameters = new SignatureSigningParameters();
			signatureSigningParameters.setSigningCredential(getConfiguration().getPrivateCredential());
			signatureSigningParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
			
			messageContext.getSubcontext(SecurityParametersContext.class, true).setSignatureSigningParameters(signatureSigningParameters);
		}

		try {
			BaseHttpServletResponseXMLMessageEncoder encoder = SAMLAuthenticationEncoderFactory.getEncoder(success, authnRequest, getContext());
			
			encoder.setMessageContext(messageContext);
			encoder.setHttpServletResponse(response);
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e) {
			logger.error("Unable to send SAML Response", e);
			// error
			StageResultCode resultCode = StageResultCode.FAT_1303;
			response.sendError(resultCode.getCode(), resultCode.toString());
		} catch (ComponentInitializationException e) {
			logger.error("Unable to send SAML Response", e);
			// error
			StageResultCode resultCode = StageResultCode.FAT_1304;
			response.sendError(resultCode.getCode(), resultCode.toString());
		}
		
		SecurityContextHolder.clearContext();
	}
	
	@Override
	public void fault(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		StageResultCode resultCode = authentication.getDetails().getResultCode();
		response.sendError(resultCode.getCode(), resultCode.toString());
	}

	private SecretKey generateSecretKey() {
		return new SecretKeySpec(generateRandomBytes(32), "AES");
	}
	
	private byte[] generateRandomBytes(int length) {
		byte[] bytes = new byte[length];
		Random r = new Random();
		r.nextBytes(bytes);
		
		return bytes;
	}

}
