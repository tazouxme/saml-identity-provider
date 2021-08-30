package com.tazouxme.idp.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.messages.MessageConstants;
import com.tazouxme.idp.messages.Messages;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class DashboardServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserAuthenticationToken authentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		
		Boolean userLoggedIn = isUserLoggedIn(authentication);
		if (userLoggedIn) {
			req.setAttribute(MessageConstants.DASHBOARD_LOGGED_IN_USER, authentication.getDetails().getIdentity().getEmail());
		}
		
		req.setAttribute(MessageConstants.DASHBOARD_LOGGED_IN, userLoggedIn);
		
		req.setAttribute(MessageConstants.DASHBOARD_ORG_INFO, Messages.find(req.getSession()).getString("dashboardServlet.organization.information"));
		req.setAttribute(MessageConstants.DASHBOARD_ORG_CLAIMS, Messages.find(req.getSession()).getString("dashboardServlet.organization.claims"));
		req.setAttribute(MessageConstants.DASHBOARD_ORG_ROLES, Messages.find(req.getSession()).getString("dashboardServlet.organization.roles"));
		req.setAttribute(MessageConstants.DASHBOARD_USERS_INFO, Messages.find(req.getSession()).getString("dashboardServlet.users.information"));
		req.setAttribute(MessageConstants.DASHBOARD_USERS_CLAIMS, Messages.find(req.getSession()).getString("dashboardServlet.users.claims"));
		req.setAttribute(MessageConstants.DASHBOARD_APPS_INFO, Messages.find(req.getSession()).getString("dashboardServlet.applications.information"));
		req.setAttribute(MessageConstants.DASHBOARD_APPS_ACCESS, Messages.find(req.getSession()).getString("dashboardServlet.applications.access"));
		req.setAttribute(MessageConstants.DASHBOARD_APPS_CLAIMS, Messages.find(req.getSession()).getString("dashboardServlet.applications.claims"));
		
		req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
	}
	
	private Boolean isUserLoggedIn(UserAuthenticationToken authentication) {
		return authentication != null && UserAuthenticationPhase.WEB_PAGE_ACCESS.equals(authentication.getDetails().getPhase());
	}

}
