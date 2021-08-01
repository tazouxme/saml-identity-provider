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

public class LoginAuthenticationFilter extends AbstractIdentityProviderFilter {

	public LoginAuthenticationFilter() {
		super(new AntPathRequestMatcher("/login", "GET"));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		UserAuthenticationToken startAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		boolean isAuthnRequestPresent = startAuthentication != null && startAuthentication.getDetails().getParameters() != null &&
				startAuthentication.getDetails().getParameters().getAuthnRequest() != null;
		
		boolean isSPinitialization = isAuthnRequestPresent &&
				UserAuthenticationPhase.MUST_AUTHENTICATE.equals(startAuthentication.getDetails().getPhase());
		
		if (!isSPinitialization) {
			logger.info("IDP Initialization process");
			SecurityContextHolder.clearContext();
			
			UserAuthenticationToken authentication = new UserAuthenticationToken();
			authentication.getDetails().setResultCode(StageResultCode.FAT_1001);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			chain.doFilter(req, res);
			return;
		} else {
			logger.info("SP Initialization process");
			
		}
		
		// display authenticate page
		req.getRequestDispatcher("/authenticate.jsp").forward(req, res);
	}

}
