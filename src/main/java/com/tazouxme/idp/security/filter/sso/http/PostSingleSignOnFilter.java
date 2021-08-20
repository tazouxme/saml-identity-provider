package com.tazouxme.idp.security.filter.sso.http;

public class PostSingleSignOnFilter extends HttpSingleSignOnFilter {

	public PostSingleSignOnFilter() {
		super("/sso", "POST");
	}

}
