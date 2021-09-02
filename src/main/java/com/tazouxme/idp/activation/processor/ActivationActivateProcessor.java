package com.tazouxme.idp.activation.processor;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.ActivationException;
import com.tazouxme.idp.application.exception.OrganizationException;
import com.tazouxme.idp.application.exception.UserException;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;

public class ActivationActivateProcessor extends AbstractActivationProcessor {

	public ActivationActivateProcessor(ApplicationContext context) {
		super(context);
	}
	
	@Override
	public void activate(String[] codes) throws ServletException, IOException {
		if (codes.length != 1) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Two codes should be provided");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		IIdentityProviderApplication application = getApplicationContext().getBean(IIdentityProviderApplication.class);
		
		try {
			Activation activation = application.findActivationByExternalId(codes[0]);
			if (!IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE.equals(activation.getStep())) {
				return;
			}
			
			Organization organization = activateOrganization(activation.getOrganizationExternalId());
			activateUser(activation.getUserExternalId(), activation.getOrganizationExternalId());
			
			application.deleteActivation(activation.getExternalId(), organization);
		} catch (ActivationException | UserException | OrganizationException e) {
			getServletRequest().setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to activate the account");
			getServletRequest().getRequestDispatcher("/register.jsp").forward(getServletRequest(), getServletResponse());
			return;
		}
		
		// redirect to admin
		getServletResponse().sendRedirect("./dashboard");
	}
	
	private Organization activateOrganization(String organizationId) throws OrganizationException {
		IOrganizationBo organizationBo = getApplicationContext().getBean(IOrganizationBo.class);
		Organization organization = organizationBo.findByExternalId(organizationId);
		organization.setEnabled(true);
		
		return organizationBo.update(organization);
	}
	
	private User activateUser(String userId, String organizationId) throws UserException {
		IUserBo userBo = getApplicationContext().getBean(IUserBo.class);
		User user = userBo.findByExternalId(userId, organizationId);
		user.setEnabled(true);
		
		return userBo.update(user);
	}

}
