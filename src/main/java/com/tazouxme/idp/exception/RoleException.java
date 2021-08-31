package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class RoleException extends AbstractIdentityProviderException {
	
	public RoleException(String message) {
		super(message);
	}
	
	public RoleException(String message, Throwable e) {
		super(message, e);
	}

}
