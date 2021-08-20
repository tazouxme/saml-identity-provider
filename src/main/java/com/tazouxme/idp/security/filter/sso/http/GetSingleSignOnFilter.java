package com.tazouxme.idp.security.filter.sso.http;

public class GetSingleSignOnFilter extends HttpSingleSignOnFilter {

	public GetSingleSignOnFilter() {
		super("/sso", "GET");
	}

}
