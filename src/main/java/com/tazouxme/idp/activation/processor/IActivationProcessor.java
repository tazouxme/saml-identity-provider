package com.tazouxme.idp.activation.processor;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IActivationProcessor {
	
	/**
	 * Activates the Organization / User using the provided codes
	 * @param codes
	 * @throws ServletException
	 * @throws IOException
	 */
	public void activate(String[] codes) throws ServletException, IOException;

}
