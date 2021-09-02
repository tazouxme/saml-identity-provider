package com.tazouxme.idp.util;

import java.util.List;

import com.tazouxme.idp.sanitizer.CertificateSanitizer;
import com.tazouxme.idp.sanitizer.EmptySanitizer;
import com.tazouxme.idp.sanitizer.EqualsStringSanitizer;
import com.tazouxme.idp.sanitizer.NonEmptySanitizer;
import com.tazouxme.idp.sanitizer.NonNullSanitizer;
import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.entity.Equality;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;
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
		if (sanitizer == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Sanitizer is null"));
			return result;
		}
		
		if (values == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Values is null"));
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
	public static SanitizerValidationResult sanitizeEmpty(String... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Values is null"));
			return result;
		}
		
		for (String value : values) {
			result.addValidation(new EmptySanitizer().sanitize(value));
		}
		
		return result;
	}
	
	/**
	 * Helper method to sanitize equality between two String with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeEquals(String v1, String v2) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		result.addValidation(new EqualsStringSanitizer().sanitize(new Equality<String>(v1, v2)));
		
		return result;
	}
	
	/**
	 * Helper method to sanitize more than one non null values with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeNonNull(List<?> values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Values is null"));
			return result;
		}
		
		for (Object value : values) {
			result.addValidation(new NonNullSanitizer().sanitize(value));
		}
		
		return result;
	}
	
	/**
	 * Helper method to sanitize more than one non null values with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeNonNull(Object... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Values is null"));
			return result;
		}
		
		for (Object value : values) {
			result.addValidation(new NonNullSanitizer().sanitize(value));
		}
		
		return result;
	}
	
	/**
	 * Helper method to sanitize more than one non empty values with one Sanitizer
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeNonEmpty(String... values) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		if (values == null) {
			result.addValidation(new SanitizerValidationImpl(Severity.ERROR, "Values is null"));
			return result;
		}
		
		for (String value : values) {
			result.addValidation(new NonEmptySanitizer().sanitize(value));
		}
		
		return result;
	}
	
	/**
	 * Helper method to sanitize a Certificate value
	 * @param sanitizer
	 * @param values
	 * @return
	 */
	public static SanitizerValidationResult sanitizeNonEmptyCertificate(String value) {
		SanitizerValidationResult result = new SanitizerValidationResultImpl();
		result.addValidation(new NonEmptySanitizer().sanitize(value));
		result.addValidation(new CertificateSanitizer().sanitize(value));
		
		return result;
	}

}
