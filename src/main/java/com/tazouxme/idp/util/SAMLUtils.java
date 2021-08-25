package com.tazouxme.idp.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
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

import org.bouncycastle.util.encoders.Base64;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.ArtifactResponse;
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
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.opensaml.saml.saml2.profile.impl.EncryptNameIDs;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
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
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserIdentity;

public class SAMLUtils {
	
	public static Endpoint getEndpoint(String url) {
		SingleSignOnService endpoint = buildSAMLObject(SingleSignOnService.class);
		endpoint.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		endpoint.setLocation(url);

		return endpoint;
	}
	
	public static RequestAbstractType unmarshallAuthnRequest(byte[] saml, boolean inflate) throws StageException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        
        InputSource input = null;
        Document document = null;
		try {
			if (inflate) {
				input = new InputSource(new InputStreamReader(
					new InflaterInputStream(new ByteArrayInputStream(saml), new Inflater(true)), "UTF-8"));
			} else {
				input = new InputSource(new ByteArrayInputStream(saml));
			}
			
			input.setEncoding("UTF-8");
			document = documentBuilderFactory.newDocumentBuilder().parse(input);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0801);
		}
        
        Element element = document.getDocumentElement();
        Unmarshaller unmarshaller = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element);
        
		try {
			return (RequestAbstractType) unmarshaller.unmarshall(element);
		} catch (UnmarshallingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0802);
		} 
	}
	
	public static ArtifactResponse buildArtifactResponse(StageParameters params, StageResultCode stageCode) {
		ArtifactResolve artifactResolve = params.getArtifactResolve();
		String idp = params.getIdpUrn();

		ArtifactResponse response = buildSAMLObject(ArtifactResponse.class);
		response.setID(IDUtils.generateId("RES_", 4));
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(Instant.now());
		response.setInResponseTo(artifactResolve.getID());
		response.setIssuer(buildIssuer(idp));
		response.setStatus(buildStatus(stageCode));
		
		return response;
	}
	
	public static Response buildResponse(StageParameters params, UserIdentity identity, String role, StageResultCode stageCode, String id) throws StageException {
		String idp = params.getIdpUrn();

		Response response = buildSAMLObject(Response.class);
		response.setID(IDUtils.generateId("RES_", 4));
		response.setVersion(SAMLVersion.VERSION_20);
		response.setIssueInstant(Instant.now());
		response.setInResponseTo(id);
		response.setIssuer(buildIssuer(idp));
		response.setStatus(buildStatus(stageCode));
		
		if (stageCode.equals(StageResultCode.OK)) {
			response.getAssertions().add(buildAssertion(params, identity, role));
		}
		
		return response;
	}
	
	public static LogoutRequest buildLogoutRequest(NameID nameId, String idp, String logoutUrl, Credential idpPrivateCredential) {
		LogoutRequest request = buildSAMLObject(LogoutRequest.class);
		request.setID(IDUtils.generateId("REQ_", 4));
		request.setVersion(SAMLVersion.VERSION_20);
		request.setIssueInstant(Instant.now());
		request.setDestination(logoutUrl);
		request.setIssuer(buildIssuer(idp));
		request.setNameID(buildNameID(nameId.getFormat(), nameId.getValue()));
		
		Signature signature = buildSAMLObject(Signature.class);
		signature.setSigningCredential(idpPrivateCredential);
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		
		request.setSignature(signature);
		
		try {
			XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(request).marshall(request);
			Signer.signObject(signature);
		} catch (MarshallingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0803);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0804);
		}
		
		return request;
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
	
	private static Assertion buildAssertion(StageParameters stageParams, UserIdentity identity, String role) throws StageException {
		AuthnRequest authnRequest = stageParams.getAuthnRequest();
		String idp = stageParams.getIdpUrn();

		Assertion assertion = buildSAMLObject(Assertion.class);
		assertion.setID(UUID.randomUUID().toString());
		assertion.setVersion(SAMLVersion.VERSION_20);
		assertion.setIssueInstant(Instant.now());
		assertion.setIssuer(buildIssuer(idp));
		assertion.setSubject(buildSubject(authnRequest, identity, stageParams.getApplication().getUrn()));
		assertion.setConditions(buildConditions(authnRequest.getIssuer().getValue()));
		assertion.getAuthnStatements().add(buildAuthnStatement(authnRequest.getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI()));
		assertion.getAttributeStatements().add(buildAttributeStatement(identity.getClaims(), role));
		
		if (NameIDType.ENCRYPTED.equals(authnRequest.getNameIDPolicy().getFormat())) {
			// encrypt
			BasicKeyInfoGeneratorFactory factory = new BasicKeyInfoGeneratorFactory();
			
			EncryptionParameters params = new EncryptionParameters();
			params.setDataEncryptionAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
			params.setDataEncryptionCredential(new BasicCredential(stageParams.getSecretKey()));
			params.setDataKeyInfoGenerator(factory.newInstance());
			params.setKeyTransportEncryptionAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
			params.setKeyTransportEncryptionCredential(obtainPublicCredential(stageParams.getOrganization().getCertificate()));
			params.setKeyTransportKeyInfoGenerator(factory.newInstance());
			
			EncryptionContext encryptCtx = new EncryptionContext();
			encryptCtx.setIdentifierEncryptionParameters(params);
			
			MessageContext messageContext = new MessageContext();
			messageContext.setMessage(assertion);
			messageContext.addSubcontext(encryptCtx);
			
			ProfileRequestContext ctx = new ProfileRequestContext();
			ctx.setOutboundMessageContext(messageContext);
			
			EncryptNameIDs encrypter = new EncryptNameIDs();
			encrypter.execute(ctx);
		}
		
		if (!SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(authnRequest.getProtocolBinding())) {
			Signature signature = buildSAMLObject(Signature.class);
			signature.setSigningCredential(stageParams.getPrivateCredential());
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
		}
		
		return assertion;
	}
	
	private static Subject buildSubject(AuthnRequest authnRequest, UserIdentity identity, String applicationUrn) {
		NameID nameId = null;
		String nameIDPolicy = authnRequest.getNameIDPolicy().getFormat();
		
		if (NameIDType.EMAIL.equals(nameIDPolicy) || NameIDType.ENCRYPTED.equals(nameIDPolicy) || NameIDType.UNSPECIFIED.equals(nameIDPolicy)) {
			nameId = buildNameID(nameIDPolicy, identity.getEmail());
		} else if (NameIDType.TRANSIENT.equals(nameIDPolicy)) {
			nameId = buildNameID(nameIDPolicy, identity.getUserId());
		} else if (NameIDType.PERSISTENT.equals(nameIDPolicy)) {
			nameId = buildNameID(nameIDPolicy, identity.getFederatedUserId());
		} else if (NameIDType.ENTITY.equals(nameIDPolicy)) {
			nameId = buildNameID(nameIDPolicy, applicationUrn);
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
	
	public static NameID buildNameID(String nameIDPolicy, String nameIDValue) {
		NameID nameId = buildSAMLObject(NameID.class);
		
		if (NameIDType.EMAIL.equals(nameIDPolicy) || NameIDType.ENCRYPTED.equals(nameIDPolicy) || NameIDType.UNSPECIFIED.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.EMAIL);
			nameId.setValue(nameIDValue);
		} else if (NameIDType.TRANSIENT.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.TRANSIENT);
			nameId.setValue(nameIDValue);
		} else if (NameIDType.PERSISTENT.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.PERSISTENT);
			nameId.setValue(nameIDValue);
		} else if (NameIDType.ENTITY.equals(nameIDPolicy)) {
			nameId.setFormat(NameID.ENTITY);
			nameId.setValue(nameIDValue);
		}
		
		return nameId;
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
	
	private static Credential obtainPublicCredential(String certificate) {
		InputStream in = new ByteArrayInputStream(Base64.decode(certificate));
		
		try {
			X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);
			return new BasicCredential(KeyFactory.getInstance("RSA", "BC").generatePublic(new X509EncodedKeySpec(Base64.decode(cert.getPublicKey().getEncoded()))));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchProviderException | CertificateException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0805);
		}
	}

}
