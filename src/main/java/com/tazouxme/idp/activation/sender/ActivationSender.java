package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActivationSender {
	
	/**
	 * Send the activation link
	 * @param req
	 * @param res
	 * @param link
	 * @throws ServletException
	 * @throws IOException
	 */
	public void send(HttpServletRequest req, HttpServletResponse res, String link) throws ServletException, IOException;

}
