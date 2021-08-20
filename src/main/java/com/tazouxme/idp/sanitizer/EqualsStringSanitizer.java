package com.tazouxme.idp.sanitizer;

import com.tazouxme.idp.sanitizer.entity.Equality;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;

public class EqualsStringSanitizer implements Sanitizer<Equality<String>> {
	
	public EqualsStringSanitizer() {
		super();
	}

	@Override
	public SanitizerValidation sanitize(Equality<String> eq) {
		if (!eq.areEquals()) {
			return new SanitizerValidationImpl(Severity.ERROR, "Value must be equal");
		}
		
		return new SanitizerValidationImpl(Severity.OK, "");
	}

}
