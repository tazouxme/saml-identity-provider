package com.tazouxme.idp.exception;

public class SessionException extends Exception {
	
	public SessionException(String message) {
		super(message);
	}
	
	public SessionException(String message, Throwable e) {
		super(message, e);
	}

}
