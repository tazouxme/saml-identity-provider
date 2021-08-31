package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class AccessException extends AbstractIdentityProviderException {
	
	public AccessException(String message) {
		super(message);
	}
	
	public AccessException(String message, Throwable e) {
		super(message, e);
	}

}
