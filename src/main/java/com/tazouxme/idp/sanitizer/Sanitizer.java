package com.tazouxme.idp.sanitizer;

import com.tazouxme.idp.sanitizer.validation.SanitizerValidation;

public interface Sanitizer {
	
	public static final RegexSanitizerRule DOMAIN_REGEX = 
			new RegexSanitizerRule("^([a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}$", "Domain not correctly formed");
	public static final RegexSanitizerRule PASSWORD_REGEX = 
			new RegexSanitizerRule("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", "Password length must be min. 8 and must contain 1 letter and 1 number");
	
	/**
	 * Validates the entry value
	 * @param value
	 * @return the validation result
	 */
	public SanitizerValidation sanitize(String value);

}
