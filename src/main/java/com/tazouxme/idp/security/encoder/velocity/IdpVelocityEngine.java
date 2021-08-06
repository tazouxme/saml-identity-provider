package com.tazouxme.idp.security.encoder.velocity;

import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class IdpVelocityEngine extends VelocityEngine {
	
	public IdpVelocityEngine() {
		super();
		
		Properties props = new Properties();
		props.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		props.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
		
		setProperties(props);
	}

}
