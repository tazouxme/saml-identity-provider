package com.tazouxme.idp.security.filter.login;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

public class LoginAuthenticationFilter extends AbstractIdentityProviderFilter {

	public LoginAuthenticationFilter() {
		super(new AntPathRequestMatcher("/login", "GET"));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		UserAuthenticationToken startAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		if (startAuthentication == null || startAuthentication.getDetails().getParameters() == null ||
				!UserAuthenticationPhase.MUST_AUTHENTICATE.equals(startAuthentication.getDetails().getPhase())) {
			SecurityContextHolder.clearContext();
			
			UserAuthenticationToken authentication = new UserAuthenticationToken();
			authentication.getDetails().setResultCode(StageResultCode.FAT_1001);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			chain.doFilter(req, res);
			return;
		}
		
		if (UserAuthenticationType.SAML.equals(startAuthentication.getDetails().getType())) {
			logger.info("SP Initialization process");

		} else {
			logger.info("IdP Initialization process");
			
		}
		
		// display authenticate page
		req.getRequestDispatcher("/authenticate.jsp").forward(req, res);
	}

}
