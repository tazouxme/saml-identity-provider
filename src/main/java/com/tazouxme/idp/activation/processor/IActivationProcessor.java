package com.tazouxme.idp.activation.processor;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IActivationProcessor {
	
	public void activate(String[] codes) throws ServletException, IOException;

}
