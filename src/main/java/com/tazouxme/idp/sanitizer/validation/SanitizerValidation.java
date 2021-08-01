package com.tazouxme.idp.sanitizer.validation;

public interface SanitizerValidation {
	
	public enum Severity {
		OK,
		WARN,
		ERROR;
	}
	
	public Severity getSeverity();
	
	public String getMessage();

}
