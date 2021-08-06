package com.tazouxme.idp.exception;

public class ClaimException extends Exception {
	
	public ClaimException(String message) {
		super(message);
	}
	
	public ClaimException(String message, Throwable e) {
		super(message, e);
	}

}
