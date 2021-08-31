package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class StoreException extends AbstractIdentityProviderException {
	
	public StoreException(String message) {
		super(message);
	}
	
	public StoreException(String message, Throwable e) {
		super(message, e);
	}

}
