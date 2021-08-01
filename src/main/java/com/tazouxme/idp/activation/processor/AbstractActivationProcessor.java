package com.tazouxme.idp.activation.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

public abstract class AbstractActivationProcessor implements IActivationProcessor {
	
	private ApplicationContext context;
	private HttpServletRequest servletRequest;
	private HttpServletResponse servletResponse;
	
	public AbstractActivationProcessor(ApplicationContext context) {
		this.context = context;
	}
	
	public void setHttpServlet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		this.servletRequest = servletRequest;
		this.servletResponse = servletResponse;
	}
	
	protected HttpServletRequest getServletRequest() {
		return servletRequest;
	}
	
	protected HttpServletResponse getServletResponse() {
		return servletResponse;
	}
	
	protected ApplicationContext getApplicationContext() {
		return context;
	}

}
