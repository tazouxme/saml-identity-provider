package com.tazouxme.idp.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import com.tazouxme.idp.security.filter.handler.AuthenticationHandlerFilter;

public class IdentityProviderFilter extends GenericFilterBean {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private AuthenticationHandlerFilter handler;
	
	private List<AbstractIdentityProviderFilter> filters = new ArrayList<>();

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		for (AbstractIdentityProviderFilter filter : filters) {
			if (filter.isRequested(request)) {
				if (filter.doFilter(request, response, chain)) {
					handler.finalize(request, response, chain);
				}
				
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	public void setFilters(List<AbstractIdentityProviderFilter> filters) {
		this.filters = Collections.synchronizedList(filters);
	}

}
