package com.tazouxme.idp.security.filter.logout;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.core.log.LogMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;

public class LogoutAuthenticationFilter extends AbstractIdentityProviderFilter {

	public LogoutAuthenticationFilter() {
		super(new AntPathRequestMatcher("/logout", "GET"));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		if (session != null) {
			this.logger.debug(LogMessage.format("Invalidated session %s", session.getId()));
			session.invalidate();
		}
		
		SecurityContextHolder.getContext().setAuthentication(null);
		SecurityContextHolder.clearContext();
		
		response.sendRedirect("./dashboard");
	}

}
