package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ClassicAuthenticationHandler extends AbstractAuthenticationHandler {
	
	public ClassicAuthenticationHandler(ApplicationContext context) {
		super(context);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication)
			throws IOException, ServletException {
		if (UserAuthenticationPhase.MUST_AUTHENTICATE.equals(authentication.getDetails().getPhase())) {
			response.sendRedirect("./login");
			return;
		}

		if (UserAuthenticationPhase.SSO_FAILED.equals(authentication.getDetails().getPhase())) {
			authentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
			
			request.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_USER_PASS, "Error" + authentication.getDetails().getResultCode().getCode());
			request.getRequestDispatcher("/authenticate.jsp").forward(request, response);
			return;
		}
		
		if (UserAuthenticationPhase.MUST_ACTIVATE.equals(authentication.getDetails().getPhase())) {
			authentication.getDetails().setPhase(UserAuthenticationPhase.MUST_AUTHENTICATE);
			
			request.setAttribute(IdentityProviderConstants.SERVLET_ERROR_WRONG_USER_PASS, "User is not active");
			request.getRequestDispatcher("/authenticate.jsp").forward(request, response);
			return;
		}
		
		if (UserAuthenticationPhase.IS_AUTHENTICATED.equals(authentication.getDetails().getPhase())) {
			if (!isSuccessfullyLoggedIn(request, response, authentication)) {
				return;
			}
			
			authentication.getDetails().setPhase(UserAuthenticationPhase.WEB_PAGE_ACCESS);
			response.sendRedirect(authentication.getDetails().getParameters().getRedirectUrl());
		}
	}
	
	@Override
	public void fault(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		request.setAttribute("code", authentication.getDetails().getResultCode().getCode());
		request.setAttribute("reason", authentication.getDetails().getResultCode().getReason());
		request.setAttribute("status", authentication.getDetails().getResultCode().getStatus());
		
		request.getRequestDispatcher("/error.jsp").forward(request, response);
	}

}
