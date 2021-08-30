package com.tazouxme.idp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tazouxme.idp.IdentityProviderConstants;

public class ErrorServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(IdentityProviderConstants.ERROR_CODE, req.getAttribute("javax.servlet.error.status_code"));
		req.setAttribute(IdentityProviderConstants.ERROR_REASON, req.getAttribute("javax.servlet.error.message"));
		
		req.getRequestDispatcher("/error.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute(IdentityProviderConstants.ERROR_CODE, req.getAttribute("javax.servlet.error.status_code"));
		req.setAttribute(IdentityProviderConstants.ERROR_REASON, req.getAttribute("javax.servlet.error.message"));
		
		req.getRequestDispatcher("/error.jsp").forward(req, resp);
	}

}
