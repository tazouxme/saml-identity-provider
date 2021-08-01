package com.tazouxme.idp.security.filter;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.encoder.IdpHTTPPostSignEncoder;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.util.SAMLUtils;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class SAMLHandlerFilter extends GenericFilterBean {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private ApplicationContext context;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		Object security = req.getAttribute(IdentityProviderConstants.SECURITY_SAML_PROCESS);
		
		UserAuthenticationToken endAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		if (security == null || !Boolean.TRUE.equals(security) || endAuthentication == null || endAuthentication.getDetails().getPhase() == null) {
			chain.doFilter(req, res);
			return;
		}
		
		HttpServletResponse response = (HttpServletResponse) res;
		
		StageParameters parameters = endAuthentication.getDetails().getParameters();
		if (parameters == null || parameters.getAuthnRequest() == null) {
			req.setAttribute("code", endAuthentication.getDetails().getResultCode().getCode());
			req.setAttribute("reason", endAuthentication.getDetails().getResultCode().getReason());
			req.setAttribute("status", endAuthentication.getDetails().getResultCode().getStatus());
			
			req.getRequestDispatcher("/error.jsp").forward(req, res);
			return;
		}
		
		if (UserAuthenticationPhase.MUST_AUTHENTICATE.equals(endAuthentication.getDetails().getPhase())) {
			// Handle Unsecured User / Password Authentication
			if (AuthnContext.PASSWORD_AUTHN_CTX.equals(
					parameters.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
				response.sendRedirect("./login");
				return;
			}
			
			// Handle Secured User / Password Authentication
			else if ("https".equals(req.getScheme()) && AuthnContext.PPT_AUTHN_CTX.equals(
					parameters.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI())) {
				response.sendRedirect("./login");
				return;
			}
			
			endAuthentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			endAuthentication.getDetails().setResultCode(StageResultCode.FAT_1301);
		}
		
		if (UserAuthenticationPhase.MUST_ACTIVATE.equals(endAuthentication.getDetails().getPhase())) {
			endAuthentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			endAuthentication.getDetails().setResultCode(StageResultCode.FAT_1305);
		}
		
		// handle status
		Response samlResponse = SAMLUtils.buildResponse(
				endAuthentication.getDetails().getParameters().getAuthnRequest(),
				endAuthentication.getDetails().getIdentity(),
				endAuthentication.getRole(),
				endAuthentication.getDetails().getParameters().getIdpUrn(),
				endAuthentication.getDetails().getResultCode(),
				endAuthentication.getDetails().getParameters().getPrivateCredential());
		
		MessageContext messageContext = new MessageContext();
		messageContext.setMessage(samlResponse);
		messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(endAuthentication.getDetails().getParameters().getRelayStateParam());

		SAMLPeerEntityContext peerEntityContext = messageContext.getSubcontext(SAMLPeerEntityContext.class, true);
		SAMLEndpointContext endpointContext = peerEntityContext.getSubcontext(SAMLEndpointContext.class, true);
		endpointContext.setEndpoint(SAMLUtils.getEndpoint(endAuthentication.getDetails().getParameters().getAuthnRequest().getAssertionConsumerServiceURL()));
		
		if (!UserAuthenticationPhase.SSO_FAILED.equals(endAuthentication.getDetails().getPhase())) {
			setUserCookie(endAuthentication.getDetails().getParameters().getIdpDomain(),
					endAuthentication.getDetails().getParameters().getIdpPath(),
					IdentityProviderConstants.COOKIE_ORGANIZATION,
					endAuthentication.getDetails().getIdentity().getOrganizationId(), response);
			setUserCookie(endAuthentication.getDetails().getParameters().getIdpDomain(),
					endAuthentication.getDetails().getParameters().getIdpPath(),
					IdentityProviderConstants.COOKIE_USER, 
					endAuthentication.getDetails().getIdentity().getUserId(), response);
			
			try {
				setUserCookie(endAuthentication.getDetails().getParameters().getIdpDomain(),
						endAuthentication.getDetails().getParameters().getIdpPath(),
						IdentityProviderConstants.COOKIE_SIGNATURE, 
						sign(endAuthentication.getDetails().getIdentity().getToken().getBytes(), endAuthentication.getDetails().getParameters().getPrivateCredential().getPrivateKey()), response);
			} catch (Exception e) {
				// error
				StageResultCode resultCode = StageResultCode.FAT_1302;
				req.setAttribute("code", resultCode.getCode());
				req.setAttribute("reason", resultCode.getReason());
				req.setAttribute("status", resultCode.getStatus());
				
				req.getRequestDispatcher("/error.jsp").forward(req, res);
				return;
			}
		}
		
		SecurityContextHolder.clearContext();

		IdpHTTPPostSignEncoder encoder = context.getBean("httpEncoder", IdpHTTPPostSignEncoder.class);
		encoder.setMessageContext(messageContext);
		encoder.setHttpServletResponse(response);
		
		try {
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e) {
			// error
			StageResultCode resultCode = StageResultCode.FAT_1303;
			req.setAttribute("code", resultCode.getCode());
			req.setAttribute("reason", resultCode.getReason());
			req.setAttribute("status", resultCode.getStatus());
			
			req.getRequestDispatcher("/error.jsp").forward(req, res);
		} catch (ComponentInitializationException e) {
			// error
			StageResultCode resultCode = StageResultCode.FAT_1304;
			req.setAttribute("code", resultCode.getCode());
			req.setAttribute("reason", resultCode.getReason());
			req.setAttribute("status", resultCode.getStatus());
			
			req.getRequestDispatcher("/error.jsp").forward(req, res);
		}
	}
	
	private void setUserCookie(String domain, String path, String name, String value, HttpServletResponse response) {
		Cookie cookie = new Cookie(name, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setMaxAge(3600 * 24 * 7); // 7 days
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		response.addCookie(cookie);
	}
	
	public String sign(byte[] text, PrivateKey key) throws Exception {
		try {
			Signature signature = generateSignature();
			signature.initSign(key);
			signature.update(text);
			
			return new String(Base64.encode(signature.sign()));
		} catch (Exception e) {
			logger.error("Unable to sign", e);
			throw new Exception("Unable to sign", e);
		}
	}
	
	private Signature generateSignature() throws Exception {
		try {
			return Signature.getInstance("SHA256withRSA", new BouncyCastleProvider());
		} catch (Exception e) {
			logger.error("Unable to generate signature", e);
			throw new Exception("Unable to generate signature", e);
		}
	}

}
