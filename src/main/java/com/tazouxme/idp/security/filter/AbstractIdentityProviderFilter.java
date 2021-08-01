package com.tazouxme.idp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import com.tazouxme.idp.IdentityProviderConstants;

public abstract class AbstractIdentityProviderFilter extends GenericFilterBean {

	protected final Log logger = LogFactory.getLog(getClass());
	private final RequestMatcher requiresAuthenticationRequestMatcher;

	public AbstractIdentityProviderFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
		this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (!requiresAuthenticationRequestMatcher.matches(request)) {
			chain.doFilter(request, response);
			return;
		}
		
		request.setAttribute(IdentityProviderConstants.SECURITY_SAML_PROCESS, Boolean.TRUE);
		doFilterInternal(request, response, chain);
	}
	
	protected abstract void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException;

}
