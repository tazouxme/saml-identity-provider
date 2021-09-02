package com.tazouxme.idp.application.exception;

import com.tazouxme.idp.application.exception.base.AbstractIdentityProviderException;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;

public class ApplicationException extends AbstractIdentityProviderException {
	
	public ApplicationException(IdentityProviderExceptionCode code, String message) {
		super(code, message);
	}
	
	public ApplicationException(IdentityProviderExceptionCode code, String message, Throwable e) {
		super(code, message, e);
	}

}
