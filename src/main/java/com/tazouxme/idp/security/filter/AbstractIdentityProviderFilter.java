package com.tazouxme.idp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.tazouxme.idp.IdentityProviderConstants;

public abstract class AbstractIdentityProviderFilter {

	protected final Log logger = LogFactory.getLog(getClass());
	private final RequestMatcher requiresAuthenticationRequestMatcher;

	public AbstractIdentityProviderFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
	}

	public boolean doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setAttribute(IdentityProviderConstants.REQUEST_ACTIVE_AUTHENTICATION, Boolean.TRUE);
		return doFilterInternal(request, response, chain);
	}
	
	public final boolean isRequested(HttpServletRequest request) {
		return requiresAuthenticationRequestMatcher.matches(request);
	}
	
	/**
	 * Proceed with Request filtering
	 * @param req - HttpServletRequest
	 * @param res - HttpServletResponse
	 * @param chain - FilterChain
	 * @return True if the filter process must go through AuthenticationHandlerFilter, false otherwise
	 * @throws IOException
	 * @throws ServletException
	 */
	protected abstract boolean doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException;

}
