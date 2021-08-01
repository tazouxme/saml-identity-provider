package com.tazouxme.idp.sanitizer.validation;

public class SanitizerValidationImpl implements SanitizerValidation {
	
	private Severity severity;
	private String message;
	
	public SanitizerValidationImpl(Severity severity, String message) {
		this.severity = severity;
		this.message = message;
	}

	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
