package com.tazouxme.idp.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

public class IdentityProviderAuthenticationEntryPoint implements AuthenticationEntryPoint {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IdentityProviderConfiguration configuration;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		logger.info("Entering Admin authentication Process");
		
		UserAuthenticationToken authentication = new UserAuthenticationToken();
		authentication.getDetails().setParameters(new StageParameters(configuration, request.getRequestURL().toString()));
		authentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
		authentication.getDetails().setType(UserAuthenticationType.CLASSIC);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		response.sendRedirect("./login");
	}

}
