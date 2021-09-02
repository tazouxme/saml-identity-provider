package com.tazouxme.idp.application.exception.base;

import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;

public class AbstractIdentityProviderException extends Exception {
	
	private IdentityProviderExceptionCode code;
	
	public AbstractIdentityProviderException(IdentityProviderExceptionCode code, String message) {
		super(message);
		this.code = code;
	}
	
	public AbstractIdentityProviderException(IdentityProviderExceptionCode code, String message, Throwable e) {
		super(message, e);
		this.code = code;
	}
	
	public IdentityProviderExceptionCode getCode() {
		return code;
	}

}
