package com.tazouxme.idp.security.filter.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;

public class LoginAuthenticationFilter extends AbstractIdentityProviderFilter {
	
	private Map<String, ILoginAuthentication> steps = new HashMap<>();

	public LoginAuthenticationFilter(List<ILoginAuthentication> steps) {
		this("/login", steps);
	}

	public LoginAuthenticationFilter(String path, List<ILoginAuthentication> steps) {
		super(new AntPathRequestMatcher(path));
		
		for (ILoginAuthentication step : steps) {
			this.steps.put(step.getMethod(), step);
		}
	}

	@Override
	protected boolean doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		ILoginAuthentication authentication = this.steps.get(req.getMethod());
		if (authentication == null) {
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return false;
		}
		
		return authentication.doFilterInternal(req, res, chain);
	}

}
