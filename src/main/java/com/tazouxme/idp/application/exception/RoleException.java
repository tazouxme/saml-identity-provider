package com.tazouxme.idp.application.exception;

import com.tazouxme.idp.application.exception.base.AbstractIdentityProviderException;
import com.tazouxme.idp.application.exception.code.IdentityProviderExceptionCode;

public class RoleException extends AbstractIdentityProviderException {
	
	public RoleException(IdentityProviderExceptionCode code, String message) {
		super(code, message);
	}
	
	public RoleException(IdentityProviderExceptionCode code, String message, Throwable e) {
		super(code, message, e);
	}

}
