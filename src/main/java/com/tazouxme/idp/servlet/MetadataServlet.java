package com.tazouxme.idp.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.util.MetadataUtils;

public class MetadataServlet extends HttpServlet {
	
	private static final String ENTITY_ID = "https://www.tazouxme.com/saml-identity-provider";
	private static final String ENTITY_CONTEXT = ENTITY_ID + "/sso";
	
	private ApplicationContext context;
	
	@Override
	public void init() throws ServletException {
		if (context == null) {
			context = WebApplicationContextUtils.findWebApplicationContext(getServletContext());
		}
		
		super.init();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		IdentityProviderConfiguration configuration = context.getBean(IdentityProviderConfiguration.class);
		EntityDescriptor entityDescriptor = MetadataUtils.buildMetadata(ENTITY_ID, ENTITY_CONTEXT, configuration.getPublicCredential());
		
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.newDocument();
	
			XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(entityDescriptor).marshall(entityDescriptor, document);
			
			StringWriter stringWriter = new StringWriter();
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(stringWriter));
			stringWriter.close();
	
			resp.setContentType("text/xml");
			stream(new ByteArrayInputStream(stringWriter.toString().getBytes()), resp.getOutputStream());
		} catch (Exception e) {
			
		}
		
	}
	
	private long stream(InputStream input, OutputStream output) throws IOException {
	    try (
	        ReadableByteChannel inputChannel = Channels.newChannel(input);
	        WritableByteChannel outputChannel = Channels.newChannel(output);
	    ) {
	        ByteBuffer buffer = ByteBuffer.allocateDirect(10240);
	        long size = 0;

	        while (inputChannel.read(buffer) != -1) {
	            buffer.flip();
	            size += outputChannel.write(buffer);
	            buffer.clear();
	        }

	        return size;
	    }
	}

}
