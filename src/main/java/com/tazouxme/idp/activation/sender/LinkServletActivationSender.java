package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.model.User;

public class LinkServletActivationSender implements ActivationSender {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private HttpServletRequest req;
	private HttpServletResponse res;
	
	public LinkServletActivationSender(HttpServletRequest req, HttpServletResponse res) {
		this.req = req;
		this.res = res;
	}

	@Override
	public boolean send(String link, User user) throws ServletException, IOException {
		if (StringUtils.isBlank(link)) {
			logger.error("Link for activation cannot be empty");
			req.setAttribute(IdentityProviderConstants.SERVLET_ERROR_REGISTER, "Unable to generate the link for Activation");
			req.getRequestDispatcher("/register.jsp").forward(req, res);
			return false;
		}
		
		logger.info("Link for activation generated");
		req.setAttribute(IdentityProviderConstants.SERVLET_REGISTER_OK, "<a href=\"" + link + "\">Validate your instance</a>");
		req.getRequestDispatcher("/register.jsp").forward(req, res);
		
		return true;
	}

}
