package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class UserException extends AbstractIdentityProviderException {
	
	public UserException(String message) {
		super(message);
	}
	
	public UserException(String message, Throwable e) {
		super(message, e);
	}

}
