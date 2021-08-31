package com.tazouxme.idp.exception;

import com.tazouxme.idp.exception.base.AbstractIdentityProviderException;

public class OrganizationException extends AbstractIdentityProviderException {
	
	public OrganizationException(String message) {
		super(message);
	}
	
	public OrganizationException(String message, Throwable e) {
		super(message, e);
	}

}
