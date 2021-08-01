package com.tazouxme.idp.exception;

public class AccessException extends Exception {
	
	public AccessException(String message) {
		super(message);
	}
	
	public AccessException(String message, Throwable e) {
		super(message, e);
	}

}
