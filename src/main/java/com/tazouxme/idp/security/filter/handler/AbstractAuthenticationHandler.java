package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public abstract class AbstractAuthenticationHandler {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private ApplicationContext context;
	private IdentityProviderConfiguration configuration;
	
	public AbstractAuthenticationHandler(ApplicationContext context) {
		super();
		this.context = context;
	}
	
	public abstract void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication)
			throws IOException, ServletException;
	
	public abstract void fault(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication)
			throws IOException, ServletException;
	
	protected boolean isSuccessfullyLoggedIn(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws ServletException, IOException {
		if (isKeepAlive(request)) {
			setUserCookie(authentication.getDetails().getParameters().getIdpDomain(),
					authentication.getDetails().getParameters().getIdpPath(),
					IdentityProviderConstants.COOKIE_ORGANIZATION,
					authentication.getDetails().getIdentity().getOrganizationId(), response);
			setUserCookie(authentication.getDetails().getParameters().getIdpDomain(),
					authentication.getDetails().getParameters().getIdpPath(),
					IdentityProviderConstants.COOKIE_USER, 
					authentication.getDetails().getIdentity().getUserId(), response);
			
			try {
				String token = registerToken(authentication.getDetails().getIdentity().getOrganizationId(), authentication.getDetails().getIdentity().getUserId());
				
				setUserCookie(authentication.getDetails().getParameters().getIdpDomain(),
						authentication.getDetails().getParameters().getIdpPath(),
						IdentityProviderConstants.COOKIE_SIGNATURE, 
						sign(token.getBytes(), authentication.getDetails().getParameters().getPrivateCredential().getPrivateKey()), response);
			} catch (Exception e) {
				logger.error("Unable to set 'signature' Cookie", e);
				// error
				StageResultCode resultCode = StageResultCode.FAT_1302;
				response.sendError(resultCode.getCode(), resultCode.toString());
				return false;
			}
		}
		
		return true;
	}
	
	protected IdentityProviderConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = context.getBean(IdentityProviderConfiguration.class);
		}
		
		return configuration;
	}
	
	protected ApplicationContext getContext() {
		return context;
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
	
	private String sign(byte[] text, PrivateKey key) throws Exception {
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
	
	private String registerToken(String organizationId, String userId) {
		// register new token
		Session session = new Session();
		session.setOrganizationExternalId(organizationId);
		session.setUserExternalId(userId);
		session.setCreatedBy(userId);
		
		try {
			ISessionBo sessionBo = context.getBean(ISessionBo.class);
			
			try {
				sessionBo.delete(session);
			} catch (Exception e) {
				if (!(e instanceof AbstractIdentityProviderException)) {
					logger.error("Unable to delete a Session", e);
					throw e;
				}
			}
			
			return sessionBo.create(session).getToken();
		} catch (SessionException e) {
			logger.error("Unable to create a Session", e);
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_1102);
		}
	}
	
	private boolean isKeepAlive(ServletRequest request) {
		String keepAlive = request.getParameter(IdentityProviderConstants.AUTH_PARAM_KEEPALIVE);
		return keepAlive != null && "on".equals(keepAlive);
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
