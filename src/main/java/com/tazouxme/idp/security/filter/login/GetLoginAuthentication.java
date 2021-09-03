package com.tazouxme.idp.security.filter.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

public class GetLoginAuthentication implements ILoginAuthentication {

	protected final Log logger = LogFactory.getLog(getClass());

	@Override
	public boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		UserAuthenticationToken startAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		if (startAuthentication == null || startAuthentication.getDetails().getParameters() == null ||
				UserAuthenticationPhase.IS_AUTHENTICATED.equals(startAuthentication.getDetails().getPhase())) {
			response.sendRedirect("./dashboard");
			return false;
		}
		
		if (!UserAuthenticationPhase.MUST_AUTHENTICATE.equals(startAuthentication.getDetails().getPhase())) {
			SecurityContextHolder.clearContext();
			
			UserAuthenticationToken authentication = new UserAuthenticationToken();
			authentication.getDetails().setResultCode(StageResultCode.FAT_1001);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
//			chain.doFilter(request, response);
			return true;
		}
		
		if (UserAuthenticationType.SAML.equals(startAuthentication.getDetails().getType())) {
			logger.info("SP Initialization process");

		} else {
			logger.info("IdP Initialization process");
			
		}
		
		// display authenticate page
		request.getRequestDispatcher("/authenticate.jsp").forward(request, response);
		return false;
	}
	
	@Override
	public String getMethod() {
		return "GET";
	}

}
