package com.tazouxme.idp.security.filter.slo;

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

public abstract class AbstractSingleLogoutFiler extends AbstractIdentityProviderFilter {
	
	private Map<String, ISingleLogout> steps = new HashMap<>();

	public AbstractSingleLogoutFiler(String path, List<ISingleLogout> steps) {
		super(new AntPathRequestMatcher(path));
		
		for (ISingleLogout step : steps) {
			this.steps.put(step.getMethod(), step);
		}
	}

	@Override
	protected boolean doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
		ISingleLogout authentication = this.steps.get(req.getMethod());
		if (authentication == null) {
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return false;
		}
		
		return authentication.doFilterInternal(req, res, chain);
	}

}
