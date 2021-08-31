package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class ActivationException extends AbstractIdentityProviderException {
	
	public ActivationException(String message) {
		super(message);
	}
	
	public ActivationException(String message, Throwable e) {
		super(message, e);
	}

}
