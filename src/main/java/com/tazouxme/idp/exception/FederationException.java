package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class FederationException extends AbstractIdentityProviderException {
	
	public FederationException(String message) {
		super(message);
	}
	
	public FederationException(String message, Throwable e) {
		super(message, e);
	}

}
