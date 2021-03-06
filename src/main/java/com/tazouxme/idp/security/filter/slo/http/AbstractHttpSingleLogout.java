package com.tazouxme.idp.security.filter.slo.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.filter.slo.AbstractSingleLogout;
import com.tazouxme.idp.security.stage.chain.StageChain;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.util.CookieUtils;

public abstract class AbstractHttpSingleLogout extends AbstractSingleLogout {

	public AbstractHttpSingleLogout(IdentityProviderConfiguration configuration) {
		super(configuration);
	}
	
	public AbstractHttpSingleLogout(IdentityProviderConfiguration configuration, StageChain stages) {
		super(configuration, stages);
	}

	@Override
	protected StageParameters obtainStageParameters(HttpServletRequest request) {
		Cookie organizationCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_ORGANIZATION);
		Cookie userCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_USER);
		Cookie signatureCookie = CookieUtils.find(request, IdentityProviderConstants.COOKIE_SIGNATURE);
		
		return new StageParameters(
			configuration,
			request.getMethod(),
			request.getRequestURL().toString() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""),
			request.getParameter(IdentityProviderConstants.PARAM_SAML_REQUEST),
			request.getParameter(IdentityProviderConstants.PARAM_SAML_RELAY_STATE),
			organizationCookie != null ? organizationCookie.getValue() : null,
			userCookie != null ? userCookie.getValue() : null,
			signatureCookie != null ? signatureCookie.getValue() : null);
	}

	@Override
	protected void userAuthenticatedSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, UserAuthenticationToken authentication) throws IOException, ServletException {
		authentication.getDetails().setPhase(UserAuthenticationPhase.IS_AUTHENTICATED);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
//		chain.doFilter(request, response);
	}

	@Override
	protected void userAuthenticatedFail(HttpServletRequest request, HttpServletResponse response, FilterChain chain, StageException e) throws IOException, ServletException {
		UserAuthenticationToken authentication = new UserAuthenticationToken();
		authentication.getDetails().setResultCode(e.getCode());
		authentication.getDetails().setParameters(e.getParams());
		authentication.getDetails().setType(UserAuthenticationType.LOGOUT);
		
		if (StageExceptionType.FATAL.equals(e.getType()) || StageExceptionType.ACTIVATION.equals(e.getType()) || StageExceptionType.ACCESS.equals(e.getType())) {
			authentication.getDetails().setPhase(UserAuthenticationPhase.SLO_FAILED);
		}
		
		SecurityContextHolder.clearContext();
//		chain.doFilter(request, response);
	}

}
