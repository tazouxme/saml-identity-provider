package com.tazouxme.saml.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tazouxme.idp.util.IDUtils;

public class SAMLUtilsTest {
	
	public static AuthnRequest buildHttpAuthnRequest(String idpUrn, String idpAcs, String application, String authnContext) throws Exception {
		AuthnRequest auth = buildSAMLObject(AuthnRequest.class);
		auth.setID(IDUtils.generateId("ID_", 4));
		auth.setVersion(SAMLVersion.VERSION_20);
		auth.setIssueInstant(Instant.now());
		auth.setForceAuthn(false);
		auth.setIsPassive(false);
		auth.setProtocolBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		auth.setDestination(idpUrn);
		auth.setAssertionConsumerServiceURL(idpAcs);
		auth.setIssuer(buildIssuer(application));
		auth.setNameIDPolicy(buildNameIDPolicy());
		auth.setRequestedAuthnContext(buildRequestedAuthnContext(authnContext));
		
		return auth;
	}

	private static Issuer buildIssuer(String serviceUrl) {
		Issuer issuer = buildSAMLObject(Issuer.class);
		issuer.setValue(serviceUrl);

		return issuer;
	}

	private static NameIDPolicy buildNameIDPolicy() {
		NameIDPolicy nameIDPolicy = buildSAMLObject(NameIDPolicy.class);
		nameIDPolicy.setFormat(NameIDType.EMAIL);
		nameIDPolicy.setAllowCreate(false);

		return nameIDPolicy;
	}
	
	public static Response getResponse(byte[] samlResponse) {
		Document document = null;
		XMLObject responseXmlObj = null;
		
		ByteArrayInputStream is = new ByteArrayInputStream(samlResponse);
		InflaterInputStream inf = new InflaterInputStream(is, new Inflater(true));
		
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
        Reader reader = null;
		try {
			reader = new InputStreamReader(inf, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Cannot read the Input Stream", e);
		}
		
        InputSource input = new InputSource(reader);
        input.setEncoding("UTF-8");
        
		try {
			document = documentBuilderFactory.newDocumentBuilder().parse(input);
		} catch (SAXException | ParserConfigurationException | IOException e) {
			throw new RuntimeException("Cannot parse the Response XML.", e);
		}
		
        Element element = document.getDocumentElement();   
        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element);
        
		try {
			responseXmlObj = unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new RuntimeException("Cannot generate the Response from the XML.", e);
		} 
		
        return (Response) responseXmlObj;
	}

	@SuppressWarnings("removal")
	private static RequestedAuthnContext buildRequestedAuthnContext(String authnContext) {
		AuthnContextClassRef authnContextClassRef = buildSAMLObject(AuthnContextClassRef.class);
		authnContextClassRef.setAuthnContextClassRef(authnContext);

		RequestedAuthnContext context = buildSAMLObject(RequestedAuthnContext.class);
		context.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
		context.getAuthnContextClassRefs().add(authnContextClassRef);

		return context;
	}

	@SuppressWarnings("unchecked")
	protected static <T> T buildSAMLObject(final Class<T> clazz) {
		T object = null;
		try {
			QName defaultElementName = (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
			object = (T) getBuilderFactory().getBuilder(defaultElementName).buildObject(defaultElementName);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Could not create SAML object", e);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("Could not create SAML object", e);
		}

		return object;
	}

	protected static XMLObjectBuilderFactory getBuilderFactory() {
		return XMLObjectProviderRegistrySupport.getBuilderFactory();
	}

}
