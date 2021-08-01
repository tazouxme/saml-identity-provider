package com.tazouxme.idp.security.stage.parameters;

public class StageCookieParameters {

	private String organization;
	private String user;
	private String signature;
	
	public StageCookieParameters(String organization, String user, String signature) {
		this.organization = organization;
		this.user = user;
		this.signature = signature;
	}

	public String getOrganization() {
		return organization;
	}

	public String getUser() {
		return user;
	}

	public String getSignature() {
		return signature;
	}

}
