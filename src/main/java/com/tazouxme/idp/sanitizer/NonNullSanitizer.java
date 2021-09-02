package com.tazouxme.idp.sanitizer;

import com.tazouxme.idp.sanitizer.validation.SanitizerValidation;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;

public class NonNullSanitizer implements Sanitizer<Object> {
	
	public NonNullSanitizer() {
		super();
	}

	@Override
	public SanitizerValidation sanitize(Object value) {
		if (value == null) {
			return new SanitizerValidationImpl(Severity.ERROR, "Value is null");
		}
		
		return new SanitizerValidationImpl(Severity.OK, "");
	}

}
