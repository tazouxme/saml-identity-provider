package com.tazouxme.idp.security.stage.parameters;

public class StageRequestParameters {
	
	private String urlMethod;
	private String urlParam;
	private String samlRequestParam;
	private String relayStateParam;
	
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

}
