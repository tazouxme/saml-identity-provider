package com.tazouxme.idp.sanitizer;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidation.Severity;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationImpl;

public class CertificateSanitizer implements Sanitizer<String> {
	
	public CertificateSanitizer() {
		super();
	}

	@Override
	public SanitizerValidation sanitize(String value) {
		if (StringUtils.isBlank(value)) {
			return new SanitizerValidationImpl(Severity.ERROR, "Value cannot be empty");
		}
		
		try {
			byte[] cert = Base64.decode(value);
			String certificate = new String(cert);
			
			if (!certificate.startsWith(IdentityProviderConstants.CERT_START) || !certificate.endsWith(IdentityProviderConstants.CERT_END)) {
				return new SanitizerValidationImpl(Severity.ERROR, "Malformed Certificate");
			}
		} catch (Exception e) {
			return new SanitizerValidationImpl(Severity.ERROR, e.getMessage());
		}
		
		return new SanitizerValidationImpl(Severity.OK, "");
	}

}
