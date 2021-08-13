package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.mail.IdentityProviderMail;
import com.tazouxme.idp.mail.exception.IdentityProviderMailException;
import com.tazouxme.idp.model.User;

public class MailActivationSender extends IdentityProviderMail implements ActivationSender {

	protected final Log logger = LogFactory.getLog(getClass());
	
	public MailActivationSender(String username, String password) {
		super(username, password);
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
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void postSend() throws IdentityProviderMailException {
		// Nothing do to here
	}

}
