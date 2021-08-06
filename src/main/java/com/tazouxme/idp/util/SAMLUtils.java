package com.tazouxme.idp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.token.UserIdentity;

public class SAMLUtils {
	
	public static Endpoint getEndpoint(String url) {
		SingleSignOnService endpoint = buildSAMLObject(SingleSignOnService.class);
		endpoint.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		endpoint.setLocation(url);

		return endpoint;
	}
	
	public static AuthnRequest unmarshallAuthnRequest(byte[] saml) throws StageException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
        InputSource input = null;
        Document document = null;
		try {
			input = new InputSource(new InputStreamReader(
				new InflaterInputStream(new ByteArrayInputStream(saml), new Inflater(true)), "UTF-8"));
			input.setEncoding("UTF-8");
			document = documentBuilderFactory.newDocumentBuilder().parse(input);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0801);
		}
        
        Element element = document.getDocumentElement();
        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element);
        
		try {
			return (AuthnRequest) unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0802);
		} 
	}
	
	public static Response buildResponse(AuthnRequest authnRequest, UserIdentity identity, String role, String idp, StageResultCode stageCode, Credential credential) throws StageException {
		Response response = buildSAMLObject(Response.class);
		response.setID(IDUtils.generateId("RES_", 4));
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(Instant.now());
		response.setInResponseTo(authnRequest.getID());
		response.setIssuer(buildIssuer(idp));
		response.setStatus(buildStatus(stageCode));
		
		if (stageCode.equals(StageResultCode.OK)) {
			response.getAssertions().add(buildAssertion(authnRequest, identity, role, idp, credential));
		}
		
		return response;
	}

	private static Issuer buildIssuer(String idp) {
		Issuer issuer = buildSAMLObject(Issuer.class);
		issuer.setValue(idp);

		return issuer;
	}
	
	private static Status buildStatus(StageResultCode stageCode) {
		StatusCode code = buildSAMLObject(StatusCode.class);
		code.setValue(stageCode.getStatus());
		
		Status status = buildSAMLObject(Status.class);
		status.setStatusCode(code);
		
		if (!stageCode.equals(StageResultCode.OK)) {
			StatusMessage message = buildSAMLObject(StatusMessage.class);
			message.setValue(stageCode.getCode() + ": " + stageCode.getReason());
			
			status.setStatusMessage(message);
		}
		
		return status;
	}
	
	private static Assertion buildAssertion(AuthnRequest authnRequest, UserIdentity identity, String role, String idp, Credential credential) throws StageException {
		Assertion assertion = buildSAMLObject(Assertion.class);
		assertion.setID(UUID.randomUUID().toString());
		assertion.setVersion(SAMLVersion.VERSION_20);
		assertion.setIssueInstant(Instant.now());
		assertion.setIssuer(buildIssuer(idp));
		assertion.setSubject(buildSubject(authnRequest, identity));
		assertion.setConditions(buildConditions(authnRequest.getIssuer().getValue()));
		assertion.getAuthnStatements().add(buildAuthnStatement(authnRequest.getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI()));
		assertion.getAttributeStatements().add(buildAttributeStatement(identity.getClaims(), role));
		
		Signature signature = buildSAMLObject(Signature.class);
		signature.setSigningCredential(credential);
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		
		assertion.setSignature(signature);
		
		try {
			XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(assertion).marshall(assertion);
			Signer.signObject(signature);
		} catch (MarshallingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0803);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0804);
		}
		
		return assertion;
	}
	
	private static Subject buildSubject(AuthnRequest authnRequest, UserIdentity identity) {
		NameID nameId = buildSAMLObject(NameID.class);
		String nameIDPolicy = authnRequest.getNameIDPolicy().getFormat();
		
		if (NameIDType.EMAIL.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.EMAIL);
			nameId.setValue(identity.getEmail());
		} else if (NameIDType.PERSISTENT.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.PERSISTENT);
			nameId.setValue(identity.getUserId());
		}
		
		SubjectConfirmationData subjectConfirmationData = buildSAMLObject(SubjectConfirmationData.class);
		subjectConfirmationData.setRecipient(authnRequest.getIssuer().getValue());
		subjectConfirmationData.setInResponseTo(authnRequest.getID());
		subjectConfirmationData.setNotOnOrAfter(Instant.ofEpochMilli(new Date().getTime() + 1000 * 3600 * 24));
		
		SubjectConfirmation subjectConfirmation = buildSAMLObject(SubjectConfirmation.class);
		subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
		subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);
		
		Subject subject = buildSAMLObject(Subject.class);
		subject.setNameID(nameId);
		subject.getSubjectConfirmations().add(subjectConfirmation);
		
		return subject;
	}
	
	private static Conditions buildConditions(String saas) {
		Audience audience = buildSAMLObject(Audience.class);
		audience.setURI(saas);
		
		AudienceRestriction audienceRestriction = buildSAMLObject(AudienceRestriction.class);
		audienceRestriction.getAudiences().add(audience);
		
		Conditions conditions = buildSAMLObject(Conditions.class);
		conditions.setNotBefore(Instant.now());
		conditions.setNotOnOrAfter(Instant.ofEpochMilli(new Date().getTime() + 1000 * 3600 * 24));
		conditions.getAudienceRestrictions().add(audienceRestriction);
		
		return conditions;
	}
	
	private static AuthnStatement buildAuthnStatement(String authnCtx) {
		AuthnContextClassRef authnContextClassRef = buildSAMLObject(AuthnContextClassRef.class);
		authnContextClassRef.setURI(authnCtx);
		
		AuthnContext authnContext = buildSAMLObject(AuthnContext.class);
		authnContext.setAuthnContextClassRef(authnContextClassRef);
		
		AuthnStatement authnStatement = buildSAMLObject(AuthnStatement.class);
		authnStatement.setAuthnInstant(Instant.now());
		authnStatement.setAuthnContext(authnContext);
		
		return authnStatement;
	}
	
	private static AttributeStatement buildAttributeStatement(Map<String, String> claims, String role) {
		AttributeStatement attributeStatement = buildSAMLObject(AttributeStatement.class);
		attributeStatement.getAttributes().add(buildAttribute(IdentityProviderConstants.SAML_CLAIM_ROLE, role));
		
		for (Entry<String, String> claim : claims.entrySet()) {
			attributeStatement.getAttributes().add(buildAttribute(claim.getKey(), claim.getValue()));
		}
		
		return attributeStatement;
	}
	
	private static Attribute buildAttribute(String name, String value) {
		Attribute attribute = buildSAMLObject(Attribute.class);
		attribute.setName(name);
		attribute.setNameFormat(Attribute.BASIC);
		
		XSAnyBuilder sb = (XSAnyBuilder) getBuilderFactory().getBuilder(XSAny.TYPE_NAME);
		XSAny attributeValue = sb.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSAny.TYPE_NAME);
		attributeValue.setTextContent(value);
		attribute.getAttributeValues().add(attributeValue);
		
		return attribute;
	}

	@SuppressWarnings("unchecked")
	public static <T> T buildSAMLObject(final Class<T> clazz) {
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
