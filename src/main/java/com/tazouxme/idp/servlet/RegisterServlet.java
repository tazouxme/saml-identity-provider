package com.tazouxme.idp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.activation.sender.ActivationSender;
import com.tazouxme.idp.activation.sender.LinkActivationSender;
import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.sanitizer.NonEmptySanitizer;
import com.tazouxme.idp.sanitizer.Sanitizer;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResult;
import com.tazouxme.idp.sanitizer.validation.SanitizerValidationResultImpl;
import com.tazouxme.idp.util.SanitizerUtils;

public class RegisterServlet extends HttpServlet {
	
	private ApplicationContext context;
	
	private IActivationBo activationBo;
	private IOrganizationBo organizationBo;
	private IUserBo userBo;
	
	@Override
	public void init() throws ServletException {
		if (context == null) {
			context = WebApplicationContextUtils.findWebApplicationContext(getServletContext());
		}
		
		activationBo = context.getBean(IActivationBo.class);
		organizationBo = context.getBean(IOrganizationBo.class);
		userBo = context.getBean(IUserBo.class);
		
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("/register.jsp").forward(req, resp);
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
			User user = register(organizationCode, organizationDomain, username, password);
			
			ActivationSender sender = new LinkActivationSender();
			sender.send(req, res, generateActivationAccess(req, user));
		} catch (OrganizationException | UserException e) {
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to register");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
		}
	}
	
	private boolean existOrganization(String domain) {
		try {
			return organizationBo.findByDomain(domain) != null;
		} catch (OrganizationException e) {
			return false;
		}
	}
	
	private User register(String organizationCode, String organizationDomain, String username, String password) throws OrganizationException, UserException {
		Organization organization = new Organization();
		organization.setCode(organizationCode);
		organization.setName(organizationCode);
		organization.setDomain(organizationDomain);
		organization.setPublicKey("");
		organization.setEnabled(false);
		
		try {
			organization = organizationBo.create(organization);
		} catch (OrganizationException e) {
			throw e;
		}
		
		User user = new User();
		user.setUsername(username);
		user.setEmail(username + "@" + organizationDomain);
		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(6)));
		user.setEnabled(false);
		user.setOrganization(organization);
		
		try {
			return userBo.create(user);
		} catch (UserException e) {
			throw e;
		}
	}
	
	private Activation createActivation(String organizationId, String userId) {
		Activation activation = new Activation();
		activation.setOrganizationExternalId(organizationId);
		activation.setUserExternalId(userId);
		activation.setStep(IdentityProviderConstants.ACTIVATION_CONST_ACTIVATE);
		
		try {
			return activationBo.create(activation);
		} catch (ActivationException e) {
			return null;
		}
	}
	
	private String generateActivationAccess(HttpServletRequest req, User user) {
		Activation activation = createActivation(user.getOrganization().getExternalId(), user.getExternalId());
		
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

}
