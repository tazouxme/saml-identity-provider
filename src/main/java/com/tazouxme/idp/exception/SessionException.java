package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class SessionException extends AbstractIdentityProviderException {
	
	public SessionException(String message) {
		super(message);
	}
	
	public SessionException(String message, Throwable e) {
		super(message, e);
	}

}
