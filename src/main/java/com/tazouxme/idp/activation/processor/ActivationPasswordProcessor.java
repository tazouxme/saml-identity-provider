package com.tazouxme.idp.activation.processor;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.ActivationException;
import com.tazouxme.idp.model.Activation;

public class ActivationPasswordProcessor extends AbstractActivationProcessor {

	public ActivationPasswordProcessor(ApplicationContext context) {
		super(context);
	}
	
	@Override
	public void activate(String[] codes) throws ServletException, IOException {
		IIdentityProviderApplication application = getApplicationContext().getBean(IIdentityProviderApplication.class);
		
		if (codes.length != 1) {
			return;
		}
		
		String code = codes[0];
		try {
			Activation activation = application.findActivationByExternalId(code);
			if (!IdentityProviderConstants.ACTIVATION_CONST_PASSWORD.equals(activation.getStep())) {
				getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Activation step is not correct");
				getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
				return;
			}
			
			// change password screen
			getServletRequest().setAttribute(IdentityProviderConstants.ACTIVATION_PARAM_ACTION, IdentityProviderConstants.ACTIVATION_CONST_PASSWORD);
			getServletRequest().setAttribute(IdentityProviderConstants.AUTH_PARAM_ORGANIZATION, activation.getOrganizationExternalId());
			getServletRequest().setAttribute(IdentityProviderConstants.AUTH_PARAM_USERNAME, activation.getUserExternalId());
			
			getServletRequest().getRequestDispatcher("/password.jsp").forward(getServletRequest(), getServletResponse());
		} catch (ActivationException e) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to get the Activation process");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
		}
	}

}
