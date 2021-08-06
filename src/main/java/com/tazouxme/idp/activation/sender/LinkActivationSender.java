package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.IdentityProviderConstants;

public class LinkActivationSender implements ActivationSender {

	protected final Log logger = LogFactory.getLog(getClass());

	@Override
	public void send(HttpServletRequest req, HttpServletResponse res, String link) throws ServletException, IOException {
		if (link == null) {
			logger.error("Link for activation cannot be empty");
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to generate the link for Activation");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return;
		}
		
		logger.info("Link for activation generated");
		req.setAttribute(IdentityProviderConstants.SERVLET_REGISTER_OK, "<a href=\"" + link + "\">Validate your instance</a>");
		req.getRequestDispatcher("/register.jsp").forward(req, res);
	}

}
