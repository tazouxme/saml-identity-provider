package com.tazouxme.idp.security.filter.sso.http;

import java.util.List;

import com.tazouxme.idp.security.filter.sso.AbstractSingleSignOnFilter;
import com.tazouxme.idp.security.filter.sso.ISingleSignOn;

public class HttpSingleSignOnFilter extends AbstractSingleSignOnFilter {

	public HttpSingleSignOnFilter(List<ISingleSignOn> steps) {
		this("/sso", steps);
	}

	public HttpSingleSignOnFilter(String path, List<ISingleSignOn> steps) {
		super(path, steps);
	}

}
