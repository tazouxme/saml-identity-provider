package com.tazouxme.idp.mail.exception;

public class IdentityProviderMailException extends Exception {
	
	public IdentityProviderMailException(String message) {
		super(message);
	}
	
	public IdentityProviderMailException(String message, Throwable e) {
		super(message, e);
	}
	
}
