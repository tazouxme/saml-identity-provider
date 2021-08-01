package com.tazouxme.idp.activation.sender;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ActivationSender {
	
	public void send(HttpServletRequest req, HttpServletResponse res, String link) throws ServletException, IOException;

}
