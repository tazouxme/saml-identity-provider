package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class ApplicationException extends AbstractIdentityProviderException {
	
	public ApplicationException(String message) {
		super(message);
	}
	
	public ApplicationException(String message, Throwable e) {
		super(message, e);
	}

}
