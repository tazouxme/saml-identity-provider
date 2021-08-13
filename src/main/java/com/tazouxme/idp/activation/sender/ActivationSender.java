package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;

import com.tazouxme.idp.model.User;

public interface ActivationSender {
	
	/**
	 * Send the activation link
	 * @param link - Then HTTP link to redirect the User for activation
	 * @param user - The targeted User
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean send(String link, User user) throws ServletException, IOException;

}
