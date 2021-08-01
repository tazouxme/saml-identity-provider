package com.tazouxme.idp.security.filter.sso;

public class PostSingleSignOnFilter extends AbstractSingleSignOnFilter {

	public PostSingleSignOnFilter() {
		super("/sso", "POST");
	}

}
