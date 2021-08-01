package com.tazouxme.idp.sanitizer;

import org.apache.commons.lang3.StringUtils;

import com.tazouxme.idp.sanitizer.validation.SanitizerValidation;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;

public class NonEmptySanitizer implements Sanitizer {
	
	private RegexSanitizerRule rule;
	
	public NonEmptySanitizer() {
		this(null);
	}
	
	public NonEmptySanitizer(RegexSanitizerRule rule) {
		this.rule = rule;
	}

	@Override
	public SanitizerValidation sanitize(String value) {
		if (StringUtils.isBlank(value)) {
			return new SanitizerValidationImpl(Severity.ERROR, "Value cannot be empty");
		}
		
		if (rule != null && !value.matches(rule.getRegex())) {
			return new SanitizerValidationImpl(Severity.ERROR, rule.getMessage());
		}
		
		return new SanitizerValidationImpl(Severity.OK, "");
	}

}
