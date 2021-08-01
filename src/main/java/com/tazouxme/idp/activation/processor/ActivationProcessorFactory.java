package com.tazouxme.idp.activation.processor;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConstants;

public class ActivationProcessorFactory {
	
	public static AbstractActivationProcessor get(String action, ApplicationContext context) {
		if (IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE.equals(action)) {
			return new ActivationActivateProcessor(context);
		}
		if (IdentityProviderConstants.ACTIVATION_CONST_PASSWORD.equals(action)) {
			return new ActivationPasswordProcessor(context);
		}
		
		return null;
	}

}
