package com.tazouxme.idp.security.filter.handler;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.security.token.UserAuthenticationType;

public class AuthenticationHandlerFactory {
	
	public static AbstractAuthenticationHandler get(UserAuthenticationType type, ApplicationContext context) {
		if (UserAuthenticationType.SOAP.equals(type)) {
			return new SOAPAuthenticationHandler(context);
		} else if (UserAuthenticationType.SAML.equals(type)) {
			return new SAMLAuthenticationHandler(context);
		} else if (UserAuthenticationType.CLASSIC.equals(type)) {
			return new ClassicAuthenticationHandler(context);
		}
		
		return null;
	}

}
