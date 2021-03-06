package com.tazouxme.idp.test.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tazouxme.idp.util.IDUtils;

public class SAMLUtilsTest {
	
	public static AuthnRequest buildHttpAuthnRequest(String idpUrn, String idpAcs, String application, String authnContext, String binding, String nameIDFormat) throws Exception {
		AuthnRequest auth = buildSAMLObject(AuthnRequest.class);
		auth.setID(IDUtils.generateId("ID_", 4));
		auth.setVersion(SAMLVersion.VERSION_20);
		auth.setIssueInstant(Instant.now());
		auth.setForceAuthn(false);
		auth.setIsPassive(false);
		auth.setProtocolBinding(binding);
		auth.setDestination(idpUrn);
		auth.setAssertionConsumerServiceURL(idpAcs);
		auth.setIssuer(buildIssuer(application));
		auth.setNameIDPolicy(buildNameIDPolicy(nameIDFormat));
		auth.setRequestedAuthnContext(buildRequestedAuthnContext(authnContext));
		
		return auth;
	}
	
	public static LogoutRequest buildHttpLogoutRequest(String idpUrn, String application, String nameIDFormat, String nameId) throws Exception {
		LogoutRequest auth = buildSAMLObject(LogoutRequest.class);
		auth.setID(IDUtils.generateId("ID_", 4));
		auth.setVersion(SAMLVersion.VERSION_20);
		auth.setIssueInstant(Instant.now());
		auth.setDestination(idpUrn);
		auth.setIssuer(buildIssuer(application));
		auth.setNameID(buildNameID(nameIDFormat, nameId));
		
		return auth;
	}
	
	public static AttributeQuery buildAttributeQuery(String idpUrn, String application, String userId, String attributeName) {
		AttributeQuery query = buildSAMLObject(AttributeQuery.class);
		query.setID(IDUtils.generateId("ID_", 4));
		query.setVersion(SAMLVersion.VERSION_20);
		query.setIssueInstant(Instant.now());
		query.setDestination(idpUrn);
		query.setIssuer(buildIssuer(application));
		query.setSubject(buildSubject(query, userId));
		query.getAttributes().add(buildAttribute(attributeName));
		
		return query;
	}

	private static Issuer buildIssuer(String serviceUrl) {
		Issuer issuer = buildSAMLObject(Issuer.class);
		issuer.setValue(serviceUrl);

		return issuer;
	}

	private static NameID buildNameID(String nameIDFormat, String nameId) {
		NameID nameID = buildSAMLObject(NameID.class);
		nameID.setFormat(nameIDFormat);
		nameID.setValue(nameId);

		return nameID;
	}

	private static NameIDPolicy buildNameIDPolicy(String nameIDFormat) {
		NameIDPolicy nameIDPolicy = buildSAMLObject(NameIDPolicy.class);
		nameIDPolicy.setFormat(nameIDFormat);
		nameIDPolicy.setAllowCreate(false);

		return nameIDPolicy;
	}
	
	private static Subject buildSubject(AttributeQuery query, String userId) {
		SubjectConfirmationData subjectConfirmationData = buildSAMLObject(SubjectConfirmationData.class);
		subjectConfirmationData.setRecipient(query.getIssuer().getValue());
		subjectConfirmationData.setInResponseTo(query.getID());
		subjectConfirmationData.setNotOnOrAfter(Instant.ofEpochMilli(new Date().getTime() + 1000 * 3600 * 24));
		
		SubjectConfirmation subjectConfirmation = buildSAMLObject(SubjectConfirmation.class);
		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
		
		Subject subject = buildSAMLObject(Subject.class);
		subject.setNameID(buildNameID(NameIDType.TRANSIENT, userId));
		subject.getSubjectConfirmations().add(subjectConfirmation);
		
		return subject;
	}
	
	private static Attribute buildAttribute(String name) {
		Attribute attribute = buildSAMLObject(Attribute.class);
		attribute.setName(name);
		attribute.setNameFormat(Attribute.BASIC);
		
		return attribute;
	}
	
	public static StatusResponseType getResponse(byte[] samlResponse) {
		Document document = null;
		XMLObject responseXmlObj = null;
		
		ByteArrayInputStream is = new ByteArrayInputStream(samlResponse);
//		InflaterInputStream inf = new InflaterInputStream(is, new Inflater(true));
		
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
//        Reader reader = null;
//		try {
//			reader = new InputStreamReader(inf, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException("Cannot read the Input Stream", e);
//		}
		
        InputSource input = new InputSource(is);
        input.setEncoding("UTF-8");
        
		try {
			document = documentBuilderFactory.newDocumentBuilder().parse(input);
		} catch (SAXException | ParserConfigurationException | IOException e) {
			throw new RuntimeException("Cannot parse the Response XML.", e);
		}
		
        Element element = document.getDocumentElement();
        writeXmlDocumentToXmlFile(document);
        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element);
        
		try {
			responseXmlObj = unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new RuntimeException("Cannot generate the Response from the XML.", e);
		} 
		
        return (StatusResponseType) responseXmlObj;
	}

	private static RequestedAuthnContext buildRequestedAuthnContext(String authnContext) {
		AuthnContextClassRef authnContextClassRef = buildSAMLObject(AuthnContextClassRef.class);
		authnContextClassRef.setURI(authnContext);

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
	
	private static void writeXmlDocumentToXmlFile(Document xmlDocument) {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer;
	    
	    try {
	        transformer = tf.newTransformer();
	         
	        // Uncomment if you do not require XML declaration
	        // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	         
	        //A character stream that collects its output in a string buffer, 
	        //which can then be used to construct a string.
	        StringWriter writer = new StringWriter();
	 
	        //transform document to string 
	        transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
	 
	        String xmlString = writer.getBuffer().toString();   
	        System.out.println(xmlString);
	    } catch (TransformerException e)  {
	        e.printStackTrace();
	    } catch (Exception e)  {
	        e.printStackTrace();
	    }
	}

}
