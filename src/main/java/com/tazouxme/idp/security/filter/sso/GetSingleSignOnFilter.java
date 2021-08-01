package com.tazouxme.idp.security.filter.sso;

public class GetSingleSignOnFilter extends AbstractSingleSignOnFilter {

	public GetSingleSignOnFilter() {
		super("/sso", "GET");
	}

}
