package com.tazouxme.idp.security.filter.slo.http;

public class PostSingleLogoutFilter extends HttpSingleLogoutFilter {

	public PostSingleLogoutFilter() {
		super("/slo", "POST");
	}

}
