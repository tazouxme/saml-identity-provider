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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.filter.handler.AbstractAuthenticationHandler;
import com.tazouxme.idp.security.filter.handler.AuthenticationHandlerFactory;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class AuthenticationHandlerFilter extends GenericFilterBean {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private ApplicationContext context;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		Object activeAuthentication = req.getAttribute(IdentityProviderConstants.REQUEST_ACTIVE_AUTHENTICATION);
		UserAuthenticationToken endAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		if (activeAuthentication == null || Boolean.FALSE.equals(activeAuthentication) || endAuthentication == null || endAuthentication.getDetails().getPhase() == null) {
			chain.doFilter(req, res);
			return;
		}
		
		AbstractAuthenticationHandler handler = AuthenticationHandlerFactory.get(endAuthentication.getDetails().getType(), context);
		
		StageParameters parameters = endAuthentication.getDetails().getParameters();
		if (parameters == null) {
			handler.fault((HttpServletRequest) req, (HttpServletResponse) res, endAuthentication);
			return;
		}
		
		logger.info("Finalizing authentication");
		handler.handle((HttpServletRequest) req, (HttpServletResponse) res, endAuthentication);
	}

}
