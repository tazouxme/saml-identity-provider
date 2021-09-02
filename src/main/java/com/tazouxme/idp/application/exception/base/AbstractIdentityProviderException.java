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
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Status: ");
		b.append(getCode().getStatus());
		b.append(", ");
		b.append("Code: ");
		b.append(getCode().getCode());
		b.append(", ");
		b.append("Message: ");
		b.append(getMessage());
		
		return b.toString();
	}

}
