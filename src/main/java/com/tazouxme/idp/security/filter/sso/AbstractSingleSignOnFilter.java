package com.tazouxme.idp.security.filter.sso;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.chain.StageChain;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.util.CookieUtils;

public abstract class AbstractSingleSignOnFilter extends AbstractIdentityProviderFilter {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private StageChain stages;
	
	@Autowired
	private IdentityProviderConfiguration configuration;

	public AbstractSingleSignOnFilter(String path, String method) {
		super(new AntPathRequestMatcher(path, method));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.info("Entering SSO Process");
		SecurityContextHolder.clearContext();

		Cookie organizationCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_ORGANIZATION);
		Cookie userCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_USER);
		Cookie signatureCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_SIGNATURE);
		
		try {
			UserAuthenticationToken authentication = stages.execute(
				new UserAuthenticationToken(),
				new StageParameters(
					configuration,
					request.getMethod(),
					request.getRequestURL().toString() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""),
					request.getParameter(IdentityProviderConstants.PARAM_SAML_REQUEST),
					request.getParameter(IdentityProviderConstants.PARAM_SAML_RELAY_STATE),
					organizationCookie != null ? organizationCookie.getValue() : null,
					userCookie != null ? userCookie.getValue() : null,
					signatureCookie != null ? signatureCookie.getValue() : null));
			
			if (UserAuthenticationPhase.USER_ACCESS_VALID.equals(authentication.getDetails().getPhase())) {
				logger.info("User Access is valid");
				// SAML Response
				authentication.getDetails().setPhase(UserAuthenticationPhase.IS_AUTHENTICATED);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
				chain.doFilter(request, response);
				return;
			}
			
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0701);
		} catch (StageException e) {
			logger.info(e.getType() + ", " + e.getCode());
			
			UserAuthenticationToken authentication = new UserAuthenticationToken();
			authentication.getDetails().setResultCode(e.getCode());
			authentication.getDetails().setParameters(e.getParams());
			
			if (StageExceptionType.FATAL.equals(e.getType()) || StageExceptionType.ACCESS.equals(e.getType())) {
				authentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			}
			
			if (StageExceptionType.AUTHENTICATION.equals(e.getType())) {
				authentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
			}
			
			if (StageExceptionType.ACTIVATION.equals(e.getType())) {
				authentication.getDetails().setPhase(UserAuthenticationPhase.MUST_ACTIVATE);
			}
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(request, response);
		}
	}
	
	public void setStages(StageChain stages) {
		this.stages = stages;
	}

}
