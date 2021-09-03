package com.tazouxme.idp.security.filter.sso.soap;

import java.util.List;

import com.tazouxme.idp.security.filter.sso.AbstractSingleSignOnFilter;
import com.tazouxme.idp.security.filter.sso.ISingleSignOn;

public class SoapSingleSignOnFilter extends AbstractSingleSignOnFilter {

	public SoapSingleSignOnFilter(List<ISingleSignOn> steps) {
		this("/sso/soap", steps);
	}

	public SoapSingleSignOnFilter(String path, List<ISingleSignOn> steps) {
		super(path, steps);
	}

}
