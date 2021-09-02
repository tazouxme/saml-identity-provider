package com.tazouxme.idp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.activation.processor.AbstractActivationProcessor;
import com.tazouxme.idp.activation.processor.ActivationProcessorFactory;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.ActivationException;
import com.tazouxme.idp.application.exception.OrganizationException;
import com.tazouxme.idp.application.exception.UserException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.sanitizer.NonEmptySanitizer;
import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResultImpl;
import com.tazouxme.idp.util.SanitizerUtils;

public class ActivationServlet extends HttpServlet {
	
	private ApplicationContext context;
	
	private IIdentityProviderApplication application;
	
	@Override
	public void init() throws ServletException {
		if (context == null) {
			context = WebApplicationContextUtils.findWebApplicationContext(getServletContext());
		}
		
		application = context.getBean(IIdentityProviderApplication.class);
		
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String[] codes = req.getParameterValues(IdentityProviderConstants.ACTIVATION_PARAM_CODE);
		if (codes == null) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "No codes provided for Activation");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}

		String action = req.getParameter(IdentityProviderConstants.ACTIVATION_PARAM_ACTION);
		if (StringUtils.isBlank(action)) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "'action' paramater not set for Activation");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}
		
		AbstractActivationProcessor activationProcessor = ActivationProcessorFactory.get(action, context);
		activationProcessor.setHttpServlet(req, res);
		activationProcessor.activate(codes);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Password change triggered
		String action = req.getParameter(IdentityProviderConstants.ACTIVATION_PARAM_ACTION);
		if (StringUtils.isBlank(action)) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_PASS, "'action' paramater not set");
			req.getRequestDispatcher("/password.jsp").forward(req, res);
			return;
		}
		
		if (!IdentityProviderConstants.ACTIVATION_CONST_PASSWORD.equals(action)) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_PASS, "'action' parameter should be 'PASSWORD'");
			req.getRequestDispatcher("/password.jsp").forward(req, res);
			return;
		}
		
		String password = req.getParameter(IdentityProviderConstants.ACTIVATION_PARAM_PASSWORD);
		String passwordCheck = req.getParameter(IdentityProviderConstants.ACTIVATION_PARAM_PASSWORD_CHECK);
		
		if (StringUtils.isBlank(password) || StringUtils.isBlank(passwordCheck) || !password.equals(passwordCheck)) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_PASS, "Passwords do not match");
			req.getRequestDispatcher("/password.jsp").forward(req, res);
			return;
		}
		
		SanitizerValidationResult result = new SanitizerValidationResultImpl(); 
		result.getValidations().addAll(SanitizerUtils.sanitize(new NonEmptySanitizer(Sanitizer.PASSWORD_REGEX), password).getValidations());
		
		if (result.hasError()) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_PASS, result.getValidations().get(0).getMessage());
			req.getRequestDispatcher("/password.jsp").forward(req, res);
			return;
		}
		
		String step = req.getParameter(IdentityProviderConstants.ACTIVATION_PARAM_ACTION);
		String organizationId = req.getParameter(IdentityProviderConstants.AUTH_PARAM_ORGANIZATION);
		String userId = req.getParameter(IdentityProviderConstants.AUTH_PARAM_USERNAME);
		
		try {
			User user = application.findUserByExternalId(userId, organizationId);
			Activation activation = application.findActivation(userId, organizationId, step);
			Organization organization = application.findOrganizationByExternalId(organizationId);
			
			application.updateUser(user.getExternalId(), BCrypt.hashpw(password, BCrypt.gensalt(6)), user.isAdministrator(), true, organization);
			application.deleteActivation(activation.getExternalId(), organization);
		} catch (UserException | ActivationException | OrganizationException e) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_PASS, "Unable to enable the User");
			req.getRequestDispatcher("/password.jsp").forward(req, res);
			return;
		}
		
		// redirect to admin
		res.sendRedirect("./dashboard");
	}

}
