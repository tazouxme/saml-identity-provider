package com.tazouxme.idp.sanitizer.validation;

public interface SanitizerValidation {
	
	public enum Severity {
		OK,
		WARN,
		ERROR;
	}
	
	/**
	 * Get the validation severity
	 * @return
	 */
	public Severity getSeverity();
	
	/**
	 * The the validation message
	 * @return
	 */
	public String getMessage();

}
