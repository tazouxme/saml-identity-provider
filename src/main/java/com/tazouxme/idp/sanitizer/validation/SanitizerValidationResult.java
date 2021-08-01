package com.tazouxme.idp.sanitizer.validation;

import java.util.List;

public interface SanitizerValidationResult {
	
	public boolean hasError();
	
	public void addValidation(SanitizerValidation validation);
	
	public List<SanitizerValidation> getValidations();
	
	public SanitizerValidation getFirstError();
	
	public List<SanitizerValidation> getErrors();

}
