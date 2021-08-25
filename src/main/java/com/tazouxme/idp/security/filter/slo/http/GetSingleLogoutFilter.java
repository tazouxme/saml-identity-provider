package com.tazouxme.idp.security.filter.slo.http;

public class GetSingleLogoutFilter extends HttpSingleLogoutFilter {

	public GetSingleLogoutFilter() {
		super("/slo", "GET");
	}

}
