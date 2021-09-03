package com.tazouxme.idp.security.filter.slo.http;

import java.util.List;

import com.tazouxme.idp.security.filter.slo.AbstractSingleLogoutFiler;
import com.tazouxme.idp.security.filter.slo.ISingleLogout;

public class HttpSingleLogoutFilter extends AbstractSingleLogoutFiler {

	public HttpSingleLogoutFilter(List<ISingleLogout> steps) {
		this("/slo", steps);
	}

	public HttpSingleLogoutFilter(String path, List<ISingleLogout> steps) {
		super(path, steps);
	}

}
