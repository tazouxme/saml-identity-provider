package com.tazouxme.idp.security.filter.handler;

import org.springframework.context.ApplicationContext;

public class AuthenticationHandlerFactory {
	
	public static AbstractAuthenticationHandler get(boolean spInitialized, ApplicationContext context) {
		if (spInitialized) {
			return new SAMLAuthenticationHandler(context);
		}
		
		return new ClassicAuthenticationHandler(context);
	}

}
