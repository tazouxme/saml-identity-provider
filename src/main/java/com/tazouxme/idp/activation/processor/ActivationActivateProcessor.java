package com.tazouxme.idp.activation.processor;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;

public class ActivationActivateProcessor extends AbstractActivationProcessor {

	public ActivationActivateProcessor(ApplicationContext context) {
		super(context);
	}
	
	@Override
	public void activate(String[] codes) throws ServletException, IOException {
		IActivationBo activationBo = getApplicationContext().getBean(IActivationBo.class);
		IOrganizationBo organizationBo = getApplicationContext().getBean(IOrganizationBo.class);
		IUserBo userBo = getApplicationContext().getBean(IUserBo.class);
		
		if (codes.length != 1) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Two codes should be provided");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		String organizationId = null;
		String userId = null;
		
		String code = codes[0];
		try {
			Activation activation = activationBo.findByExternalId(code);
			if (!IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE.equals(activation.getStep())) {
				return;
			}
			
			organizationId = activation.getOrganizationExternalId();
			userId = activation.getUserExternalId();
		} catch (ActivationException e) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to activate the account");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		try {
			Organization organization = organizationBo.findByExternalId(organizationId);
			organization.setEnabled(true);
			
			organizationBo.update(organization);
		} catch (OrganizationException e) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to activate the Organization");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		try {
			User user = userBo.findByExternalId(userId, organizationId);
			user.setEnabled(true);
			
			userBo.update(user);
		} catch (UserException e) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to activate the User");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		// redirect to admin
		getServletResponse().sendRedirect("./dashboard");
	}

}
