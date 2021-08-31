package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class ClaimException extends AbstractIdentityProviderException {
	
	public ClaimException(String message) {
		super(message);
	}
	
	public ClaimException(String message, Throwable e) {
		super(message, e);
	}

}
