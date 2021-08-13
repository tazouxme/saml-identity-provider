package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.mail.IdentityProviderMail;
import com.tazouxme.idp.mail.exception.IdentityProviderMailException;
import com.tazouxme.idp.model.User;

public class MailServletActivationSender extends IdentityProviderMail implements ActivationSender {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private HttpServletRequest req;
	private HttpServletResponse res;
	
	public MailServletActivationSender(HttpServletRequest req, HttpServletResponse res, String username, String password) {
		super(username, password);
		this.req = req;
		this.res = res;
	}
	
	@Override
	public boolean send(String link, User user) throws ServletException, IOException {
		try {
			return super.doSend(link, user);
		} catch (IdentityProviderMailException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected boolean isPreCheckFine(String link, User user) throws IdentityProviderMailException {
		if (StringUtils.isBlank(link)) {
			logger.error("Link for activation cannot be empty");
			
			try {
				req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to generate the link for Activation");
				req.getRequestDispatcher("/register.jsp").forward(req, res);
			} catch (ServletException | IOException e) {
				throw new IdentityProviderMailException("Cannot redirect after email generation failure", e);
			}
			
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void postSend() throws IdentityProviderMailException {
		logger.info("Link for activation generated and sent by mail");
		
		try {
			req.setAttribute(IdentityProviderConstants.SERVLET_REGISTER_OK, "An email was sent to your address");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
		} catch (ServletException | IOException e) {
			throw new IdentityProviderMailException("Cannot redirect after email generation", e);
		}
	}

}
