package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.security.encoder.IdpHTTPPostSignEncoder;
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
	public void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication)
			throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		
		if (parameters.getAuthnRequest() == null) {
			request.setAttribute("code", authentication.getDetails().getResultCode().getCode());
			request.setAttribute("reason", authentication.getDetails().getResultCode().getReason());
			request.setAttribute("status", authentication.getDetails().getResultCode().getStatus());
			
			request.getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}
		
		if (UserAuthenticationPhase.MUST_AUTHENTICATE.equals(authentication.getDetails().getPhase())) {
			// Handle Unsecured User / Password Authentication
			if (AuthnContext.PASSWORD_AUTHN_CTX.equals(
					parameters.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
				response.sendRedirect("./login");
				return;
			}
			
			// Handle Secured User / Password Authentication
			else if ("https".equals(request.getScheme()) && AuthnContext.PPT_AUTHN_CTX.equals(
					parameters.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
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
		
		if (!UserAuthenticationPhase.SSO_FAILED.equals(authentication.getDetails().getPhase())) {
			Set<Claim> claims = authentication.getDetails().getParameters().getApplication().getClaims();
			Set<UserDetails> details = authentication.getDetails().getParameters().getUser().getDetails();
			
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
				authentication.getDetails().getParameters().getAuthnRequest(),
				authentication.getDetails().getIdentity(),
				authentication.getRole(),
				authentication.getDetails().getParameters().getIdpUrn(),
				authentication.getDetails().getResultCode(),
				authentication.getDetails().getParameters().getPrivateCredential());
		
		MessageContext messageContext = new MessageContext();
		messageContext.setMessage(samlResponse);
		messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(authentication.getDetails().getParameters().getRelayStateParam());

		SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class, true);
		SAMLEndpointContext endpointContext = peerEntityContext.getSubcontext(SAMLEndpointContext.class, true);
		endpointContext.setEndpoint(SAMLUtils.getEndpoint(authentication.getDetails().getParameters().getAuthnRequest().getAssertionConsumerServiceURL()));
		
		SecurityContextHolder.clearContext();

		IdpHTTPPostSignEncoder encoder = getAuthenticationContext().getBean("httpEncoder", IdpHTTPPostSignEncoder.class);
		encoder.setMessageContext(messageContext);
		encoder.setHttpServletResponse(response);
		
		try {
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e) {
			logger.error("Unable to send SAML Response", e);
			// error
			StageResultCode resultCode = StageResultCode.FAT_1303;
			request.setAttribute("code", resultCode.getCode());
			request.setAttribute("reason", resultCode.getReason());
			request.setAttribute("status", resultCode.getStatus());
			
			request.getRequestDispatcher("/error.jsp").forward(request, response);
		} catch (ComponentInitializationException e) {
			logger.error("Unable to send SAML Response", e);
			// error
			StageResultCode resultCode = StageResultCode.FAT_1304;
			request.setAttribute("code", resultCode.getCode());
			request.setAttribute("reason", resultCode.getReason());
			request.setAttribute("status", resultCode.getStatus());
			
			request.getRequestDispatcher("/error.jsp").forward(request, response);
		}
	}

}
