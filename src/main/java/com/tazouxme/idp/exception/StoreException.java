package com.tazouxme.idp.exception;

public class StoreException extends Exception {
	
	public StoreException(String message) {
		super(message);
	}
	
	public StoreException(String message, Throwable e) {
		super(message, e);
	}

}
