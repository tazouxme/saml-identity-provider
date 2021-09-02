package com.tazouxme.idp.sanitizer.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;

public class SanitizerValidationResultImpl implements SanitizerValidationResult {
	
	private List<SanitizerValidation> validations = new ArrayList<>();

	@Override
	public boolean hasError() {
		return validations.stream().anyMatch(v -> v.getSeverity().equals(Severity.ERROR));
	}
	
	@Override
	public void add(SanitizerValidationResult validation) {
		for (SanitizerValidation sanitizerValidation : validation.getValidations()) {
			addValidation(sanitizerValidation);
		}
	}

	@Override
	public void addValidation(SanitizerValidation validation) {
		validations.add(validation);
	}

	@Override
	public List<SanitizerValidation> getValidations() {
		return validations;
	}
	
	@Override
	public SanitizerValidation getFirstError() {
		return validations.stream().filter(v -> Severity.ERROR.equals(v.getSeverity())).findFirst().get();
	}
	
	@Override
	public List<SanitizerValidation> getErrors() {
		return validations.stream().filter(v -> Severity.ERROR.equals(v.getSeverity())).collect(Collectors.toList());
	}

}
