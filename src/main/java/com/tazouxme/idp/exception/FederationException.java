package com.tazouxme.idp.exception;

public class FederationException extends Exception {
	
	public FederationException(String message) {
		super(message);
	}
	
	public FederationException(String message, Throwable e) {
		super(message, e);
	}

}
