package com.tazouxme.idp.security.stage.exception;

public enum StageExceptionType {
	
	ACCESS ("Access"),
	ACTIVATION ("Activation"),
	FATAL ("Fatal"),
	CREDENTIALS ("Credentials"),
	AUTHENTICATION ("Authentication");
	
	private String description;
	
	private StageExceptionType(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "Exception type: " + description;
	}

}
