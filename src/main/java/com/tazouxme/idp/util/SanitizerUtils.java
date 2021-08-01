package com.tazouxme.idp.util;

import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResultImpl;

public class SanitizerUtils {
	
	public static SanitizerValidationResult sanitize(Sanitizer sanitizer, String... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			return result;
		}
		
		for (String value : values) {
			result.addValidation(sanitizer.sanitize(value));
		}
		
		return result;
	}

}
