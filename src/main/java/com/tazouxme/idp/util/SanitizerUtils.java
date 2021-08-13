package com.tazouxme.idp.util;

import com.tazouxme.idp.sanitizer.NonEmptySanitizer;
import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResultImpl;

public class SanitizerUtils {
	
	/**
	 * Helper method to sanitize more than one values with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	@SafeVarargs
	public static <T> SanitizerValidationResult sanitize(Sanitizer<T> sanitizer, T... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			return result;
		}
		
		for (T value : values) {
			result.addValidation(sanitizer.sanitize(value));
		}
		
		return result;
	}
	
	/**
	 * Helper method to sanitize more than one values with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeNonEmpty(String... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			return result;
		}
		
		for (String value : values) {
			result.addValidation(new NonEmptySanitizer().sanitize(value));
		}
		
		return result;
	}

}
