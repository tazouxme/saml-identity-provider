package com.tazouxme.idp.exception.base;

public class AbstractIdentityProviderException extends Exception {
	
	public AbstractIdentityProviderException(String message) {
		super(message);
	}
	
	public AbstractIdentityProviderException(String message, Throwable e) {
		super(message, e);
	}

}
