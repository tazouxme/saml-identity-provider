package com.tazouxme.idp.sanitizer.validation;

import java.util.List;

public interface SanitizerValidationResult {
	
	/**
	 * Checks if the list of validation results has any error
	 * @return
	 */
	public boolean hasError();
	
	/**
	 * Add a validation result in the list
	 * @param validation
	 */
	public void addValidation(SanitizerValidation validation);
	
	/**
	 * Get all validations
	 * @return
	 */
	public List<SanitizerValidation> getValidations();
	
	/**
	 * Find the first error in the list
	 * @return
	 */
	public SanitizerValidation getFirstError();
	
	/**
	 * Get all errors from the validation list
	 * @return
	 */
	public List<SanitizerValidation> getErrors();

}
