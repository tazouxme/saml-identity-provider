package com.tazouxme.idp.application.exception;

import com.tazouxme.idp.application.exception.base.AbstractIdentityProviderException;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;

public class ClaimException extends AbstractIdentityProviderException {
	
	public ClaimException(IdentityProviderExceptionCode code, String message) {
		super(code, message);
	}
	
	public ClaimException(IdentityProviderExceptionCode code, String message, Throwable e) {
		super(code, message, e);
	}

}
