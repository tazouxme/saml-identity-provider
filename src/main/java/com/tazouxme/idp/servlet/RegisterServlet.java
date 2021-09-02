package com.tazouxme.idp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.activation.sender.ActivationSender;
import com.tazouxme.idp.activation.sender.LinkServletActivationSender;
import com.tazouxme.idp.activation.sender.MailServletActivationSender;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.ActivationException;
import com.tazouxme.idp.application.exception.ClaimException;
import com.tazouxme.idp.application.exception.OrganizationException;
import com.tazouxme.idp.application.exception.UserException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.sanitizer.NonEmptySanitizer;
import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResultImpl;
import com.tazouxme.idp.util.SanitizerUtils;

public class RegisterServlet extends HttpServlet {
	
	private ApplicationContext context;
	private IIdentityProviderApplication idpApplication;
	
	@Override
	public void init() throws ServletException {
		if (context == null) {
			context = WebApplicationContextUtils.findWebApplicationContext(getServletContext());
		}
		
		idpApplication = context.getBean(IIdentityProviderApplication.class);
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("/register.jsp").forward(req, res);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String organizationCode = req.getParameter("organization");
		String organizationDomain = req.getParameter("domain");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		SanitizerValidationResult result = new SanitizerValidationResultImpl(); 
		result.getValidations().addAll(SanitizerUtils.sanitize(new NonEmptySanitizer(), organizationCode, username).getValidations());
		result.getValidations().addAll(SanitizerUtils.sanitize(new NonEmptySanitizer(Sanitizer.DOMAIN_REGEX), organizationDomain).getValidations());
		result.getValidations().addAll(SanitizerUtils.sanitize(new NonEmptySanitizer(Sanitizer.PASSWORD_REGEX), password).getValidations());
		
		if (result.hasError()) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, result.getFirstError().getMessage());
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}
		
		// save Organization
		if (existOrganization(organizationDomain)) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Organization already exists");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}
		
		try {
			User user = register(organizationCode, organizationDomain, username, password, true);
			Activation activation = idpApplication.createActivation(user.getOrganization().getExternalId(), user.getExternalId(), IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE, user.getExternalId());
			
			ActivationSender sender = findActivationSender(req, res, getServletContext().getInitParameter("mail-username"), getServletContext().getInitParameter("mail-password"));
			sender.send(generateActivationAccess(req, user, activation), user);
		} catch (OrganizationException | UserException | ClaimException | ActivationException e) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to register");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
		}
	}
	
	private boolean existOrganization(String domain) {
		try {
			return idpApplication.findOrganizationByDomain(domain) != null;
		} catch (OrganizationException e) {
			return false;
		}
	}
	
	private User register(String organizationCode, String organizationDomain, String username, String password, boolean administrator) throws OrganizationException, UserException, ClaimException {
		return idpApplication.createUser(username, password, administrator, 
				idpApplication.createOrganization(organizationCode, organizationDomain, "SYSTEM"), null);
	}
	
	private String generateActivationAccess(HttpServletRequest req, User user, Activation activation) {
		if (activation == null) {
			return null;
		}
		
		var url = req.getRequestURL();
		url.append("?");
		url.append(IdentityProviderConstants.ACTIVATION_PARAM_ACTION);
		url.append("=");
		url.append(IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE);
		url.append("&");
		url.append(IdentityProviderConstants.ACTIVATION_PARAM_CODE);
		url.append("=");
		url.append(activation.getExternalId());
		
		return url.toString().replace("register", "activate");
	}
	
	private ActivationSender findActivationSender(HttpServletRequest req, HttpServletResponse res, String mailUsername, String mailPassword) {
		if (StringUtils.isBlank(mailUsername) || StringUtils.isBlank(mailPassword)) {
			return new LinkServletActivationSender(req, res); 
		}
		
		return new MailServletActivationSender(req, res, mailUsername, mailPassword);
	}

}
