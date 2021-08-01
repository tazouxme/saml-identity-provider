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

	/*
	@Override
	public boolean mergeTemplate(String templateName, String encoding, Context context, Writer writer)
			throws ResourceNotFoundException, ParseErrorException, MethodInvocationException {
		Resource resource = resourceLoader.getResource(templateName);
		RuntimeServices rs = RuntimeSingleton.getRuntimeServices();

		Template t = new Template();
		t.setRuntimeServices(rs);

		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding));
			t.setData(rs.parse(br, null));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		t.initDocument();
		t.merge(context, writer);

		return true;
	}
	*/

}
