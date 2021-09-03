package com.tazouxme.idp.security.filter.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.entity.PasswordEntity;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class PostLoginAuthentication implements ILoginAuthentication {

	protected final Log logger = LogFactory.getLog(getClass());

	private AuthenticationManager authenticationManager;

	@Override
	public boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		UserAuthenticationToken inAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		String organization = request.getParameter(IdentityProviderConstants.AUTH_PARAM_ORGANIZATION);
		String username = request.getParameter(IdentityProviderConstants.AUTH_PARAM_USERNAME);
		String password = request.getParameter(IdentityProviderConstants.AUTH_PARAM_PASSWORD);
		
		if (StringUtils.isEmpty(organization) || StringUtils.isEmpty(username)) {
			logger.info(StageResultCode.FAT_1101.getReason());
			inAuthentication.getDetails().setResultCode(StageResultCode.FAT_1101);
			
//			chain.doFilter(request, response);
			return true;
		}
		
		if (StringUtils.isEmpty(password)) {
			logger.info("Wrong username / password");
			// show error message to authenticate page
			inAuthentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
			
			request.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_USER_PASS, "Wrong username / password");
			request.getRequestDispatcher("/authenticate.jsp").forward(request, response);
			return false;
		}
		
		try {
			UserAuthenticationToken endAuthentication = (UserAuthenticationToken) authenticationManager.authenticate(
					new UserAuthenticationToken(username, new ObjectMapper().readValue(password.getBytes(), PasswordEntity.class)));
			
			if (UserAuthenticationPhase.IS_AUTHENTICATED.equals(endAuthentication.getDetails().getPhase())) {
				logger.info("User successfully authenticated");
				// SAML Response
				SecurityContextHolder.getContext().setAuthentication(endAuthentication);
				
//				chain.doFilter(request, response);
				return true;
			}
			
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1103);
		} catch (Exception e) {
			if (e instanceof StageException) {
				return handleStageException((StageException) e, request, response, chain);
			}
			
			logger.info("Other issue", e);
			// JSON issue, should not happen - post SAML error
			inAuthentication.getDetails().setResultCode(StageResultCode.FAT_1104);
//			chain.doFilter(request, response);
			return true;
		}
	}
	
	@Override
	public String getMethod() {
		return "POST";
	}
	
	private boolean handleStageException(StageException s, ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (StageExceptionType.CREDENTIALS.equals(s.getType())) {
			logger.info("Wrong username / password");
			// display error message on authenticate page
			UserAuthenticationToken inAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
			inAuthentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
			
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_USER_PASS, "Wrong username / password");
			req.getRequestDispatcher("/authenticate.jsp").forward(req, res);
			return false;
		}
		
		if (StageExceptionType.ACCESS.equals(s.getType()) || StageExceptionType.FATAL.equals(s.getType())) {
			logger.info(s.getCode().getReason());
			// post SAML error
			UserAuthenticationToken startAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
			startAuthentication.getDetails().setPhase(UserAuthenticationPhase.SSO_FAILED);
			startAuthentication.getDetails().setResultCode(s.getCode());
			
//			chain.doFilter(req, res);
			return true;
		}
		
		return true;
	}
	
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

}
