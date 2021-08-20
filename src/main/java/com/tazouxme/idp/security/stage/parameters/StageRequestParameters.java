package com.tazouxme.idp.security.stage.parameters;

import javax.servlet.http.HttpServletRequest;

public class StageRequestParameters {
	
	private String urlMethod;
	private String urlParam;
	private String samlRequestParam;
	private String relayStateParam;
	private HttpServletRequest request;
	
	public StageRequestParameters(HttpServletRequest request) {
		this.request = request;
	}
	
	public StageRequestParameters(String urlMethod, String urlParam, String samlRequestParam, String relayStateParam) {
		this.urlMethod = urlMethod;
		this.urlParam = urlParam;
		this.samlRequestParam = samlRequestParam;
		this.relayStateParam = relayStateParam;
	}
	
	public String getUrlMethod() {
		return urlMethod;
	}
	
	public String getUrlParam() {
		return urlParam;
	}

	public String getSamlRequestParam() {
		return samlRequestParam;
	}

	public String getRelayStateParam() {
		return relayStateParam;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

}
