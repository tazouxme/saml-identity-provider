package com.tazouxme.idp.exception;

public class UserException extends Exception {
	
	public UserException(String message) {
		super(message);
	}
	
	public UserException(String message, Throwable e) {
		super(message, e);
	}

}
