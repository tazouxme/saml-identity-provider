package com.tazouxme.idp.security.filter.slo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISingleLogout {
	
	/**
	 * Get HTTP Method applicable to this Filter
	 * @return HTTP Method
	 */
	public String getMethod();

	/**
	 * Proceed with Request filtering
	 * @param req - HttpServletRequest
	 * @param res - HttpServletResponse
	 * @param chain - FilterChain
	 * @return True if the filter process must go through AuthenticationHandlerFilter, false otherwise
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;
	
}
