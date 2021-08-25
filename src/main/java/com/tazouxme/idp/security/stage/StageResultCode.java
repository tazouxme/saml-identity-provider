package com.tazouxme.idp.security.stage;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.StatusCode;

public enum StageResultCode {
	
	OK ("OK", "Success", StatusCode.SUCCESS),

	FAT_0001 ("FAT-0001", "Authentication phase not correctly set", StatusCode.RESPONDER),
	
	// Http ValidateRequestParameters
	FAT_0101 ("FAT-0101", "SAMLRequest parameter not found", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0102 ("FAT-0102", "RelayState parameter not found", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0103 ("FAT-0103", "Only HTTP GET and POST supported", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0104 ("FAT-0104", "Captured message is not an AuthnRequest", StatusCode.REQUEST_UNSUPPORTED),
	
	// Soap ValidateRequestParameters
	FAT_0151 ("FAT-0151", "MessageDecodingException occured during ArtifactResolve capture", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0152 ("FAT-0152", "ComponentInitializationException occured during ArtifactResolve capture", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0153 ("FAT-0153", "Captured message is not an ArtifactResolve. Only ArtifactResolve supported for binding " + SAMLConstants.SAML2_SOAP11_BINDING_URI, StatusCode.REQUEST_UNSUPPORTED),
	
	// Logout ValidateRequestParameters
	FAT_0181 ("FAT-0181", "SAMLRequest parameter not found", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0182 ("FAT-0182", "RelayState parameter not found", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0183 ("FAT-0183", "Only HTTP GET and POST supported", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0184 ("FAT-0184", "Captured message is not an LogoutRequest", StatusCode.REQUEST_UNSUPPORTED),
	
	// Http ValidateRequestValues
	FAT_0203 ("FAT-0203", "AuthnRequest ID not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0204 ("FAT-0204", "AuthnRequest AssertionConsumerServiceURL not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0205 ("FAT-0205", "AuthnRequest IssueInstant not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0206 ("FAT-0206", "AuthnRequest IssueInstant is not valid", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0207 ("FAT-0207", "SAML Version must be '2.0'", StatusCode.VERSION_MISMATCH),
	FAT_0208 ("FAT-0208", "Identity Provider accepts only " + SAMLConstants.SAML2_REDIRECT_BINDING_URI + ", " + SAMLConstants.SAML2_ARTIFACT_BINDING_URI + ", "
			+ SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI + " and " + SAMLConstants.SAML2_POST_BINDING_URI, StatusCode.NO_SUPPORTED_IDP),
	FAT_0209 ("FAT-0209", "AuthnRequest Issuer not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0210 ("FAT-0210", "AuthnRequest Issuer must not be empty", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0211 ("FAT-0211", "Unknown Identity Provider", StatusCode.NO_SUPPORTED_IDP),
	FAT_0214 ("FAT-0214", "Unknown NameIDPolicy", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0215 ("FAT-0215", "Only " + NameIDType.UNSPECIFIED + ", " + NameIDType.EMAIL + ", " + NameIDType.ENCRYPTED + ", " + NameIDType.PERSISTENT + ", "
			+ NameIDType.ENTITY + " and " + NameIDType.TRANSIENT + " accepted as NameIDPolicy", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0216 ("FAT-0216", "AllowCreate is not enabled", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0217 ("FAT-0217", "No AuthnContextClassRef found", StatusCode.NO_AUTHN_CONTEXT),
	FAT_0218 ("FAT-0218", "No AuthnContextClassRef found", StatusCode.NO_AUTHN_CONTEXT),
	FAT_0219 ("FAT-0219", "Only " + AuthnContext.PPT_AUTHN_CTX + " and " + AuthnContext.PASSWORD_AUTHN_CTX + " accepted as AuthnContextClassRef", StatusCode.INVALID_ATTR_NAME_OR_VALUE),
	AUT_0201 ("AUT-0201", "AuthnRequest ForceAuthn set to 'true'", StatusCode.REQUESTER),

	// Soap ValidateRequestValues
	FAT_0253 ("FAT-0253", "ArtifactResolve ID not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0255 ("FAT-0255", "ArtifactResolve IssueInstant not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0256 ("FAT-0256", "ArtifactResolve IssueInstant is not valid", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0257 ("FAT-0257", "SAML Version must be '2.0'", StatusCode.VERSION_MISMATCH),
	FAT_0259 ("FAT-0259", "ArtifactResolve Issuer not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0260 ("FAT-0260", "ArtifactResolve Issuer must not be empty", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0261 ("FAT-0261", "Unknown Identity Provider", StatusCode.NO_SUPPORTED_IDP),
	FAT_0262 ("FAT-0262", "ArtifactResolve Artifact not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0263 ("FAT-0263", "ArtifactResolve Artifact must not be empty", StatusCode.NO_SUPPORTED_IDP),
	
	// Logout ValidateRequestValues
	FAT_0283 ("FAT-0283", "LogoutRequest ID not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0285 ("FAT-0285", "LogoutRequest IssueInstant not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0286 ("FAT-0286", "LogoutRequest IssueInstant is not valid", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0287 ("FAT-0287", "SAML Version must be '2.0'", StatusCode.VERSION_MISMATCH),
	FAT_0289 ("FAT-0289", "LogoutRequest Issuer not set", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0290 ("FAT-0290", "LogoutRequest Issuer must not be empty", StatusCode.REQUEST_UNSUPPORTED),
	FAT_0291 ("FAT-0291", "Unknown Identity Provider", StatusCode.NO_SUPPORTED_IDP),
	FAT_0294 ("FAT-0294", "Unknown NameID", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0295 ("FAT-0295", "NameID must be set", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0296 ("FAT-0296", "Only " + NameIDType.UNSPECIFIED + ", " + NameIDType.EMAIL + ", " + NameIDType.ENCRYPTED + ", " + NameIDType.PERSISTENT + ", "
			+ NameIDType.ENTITY + " and " + NameIDType.TRANSIENT + " accepted as NameIDPolicy", StatusCode.INVALID_NAMEID_POLICY),
	
	// Http ValidateUserCookies
	FAT_0302 ("FAT-0302", "User not found", StatusCode.UNKNOWN_PRINCIPAL),
	AUT_0301 ("AUT-0301", "Organization Cookie not found", StatusCode.REQUESTER),
	AUT_0302 ("AUT-0302", "User Cookie not found", StatusCode.REQUESTER),
	AUT_0303 ("AUT-0303", "Signature Cookie not found", StatusCode.REQUESTER),
	ACT_0301 ("ACT-0301", "Organization is not enabled", StatusCode.REQUESTER),
	ACT_0302 ("ACT-0302", "User is not enabled", StatusCode.REQUESTER),
	
	// Soap ValidateUserCookies
	FAT_0352 ("FAT-0352", "User not found", StatusCode.UNKNOWN_PRINCIPAL),
	FAT_0353 ("FAT-0353", "Organization Cookie not found", StatusCode.REQUESTER),
	FAT_0354 ("FAT-0354", "User Cookie not found", StatusCode.REQUESTER),
	FAT_0355 ("FAT-0355", "Signature Cookie not found", StatusCode.REQUESTER),
	ACT_0351 ("ACT-0351", "Organization is not enabled", StatusCode.REQUESTER),
	ACT_0352 ("ACT-0352", "User is not enabled", StatusCode.REQUESTER),
	
	// Logout ValidateUserCookies
	FAT_0382 ("FAT-0382", "User not found", StatusCode.UNKNOWN_PRINCIPAL),
	FAT_0383 ("FAT-0383", "Organization Cookie not found", StatusCode.REQUESTER),
	FAT_0384 ("FAT-0384", "User Cookie not found", StatusCode.REQUESTER),
	FAT_0385 ("FAT-0385", "Signature Cookie not found", StatusCode.REQUESTER),
	ACT_0381 ("ACT-0381", "Organization is not enabled", StatusCode.REQUESTER),
	ACT_0382 ("ACT-0382", "User is not enabled", StatusCode.REQUESTER),
	
	// Http ValidateUserSignature
	FAT_0402 ("FAT-0402", "InvalidKeyException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0403 ("FAT-0403", "SignatureException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0404 ("FAT-0404", "NoSuchAlgorithmException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0405 ("FAT-0405", "AuthnRequest Signature does not correspond", StatusCode.REQUESTER),
	FAT_0406 ("FAT-0406", "SignatureException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0407 ("FAT-0407", "InvalidKeyException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0408 ("FAT-0408", "NoSuchAlgorithmException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0409 ("FAT-0409", "UnsupportedEncodingException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0410 ("FAT-0410", "InvalidKeySpecException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0411 ("FAT-0411", "NoSuchProviderException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0412 ("FAT-0412", "MalformedURLException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0413 ("FAT-0413", "Parameter not found in the request", StatusCode.REQUESTER),
	FAT_0414 ("FAT-0414", "Organization must have a PublicKey to verify AuthnRequest signature", StatusCode.REQUESTER),
	FAT_0415 ("FAT-0415", "Organization must have a PublicKey to encrypt NameID", StatusCode.REQUESTER),
	FAT_0416 ("FAT-0416", "CertificateException occured during Signature verification", StatusCode.REQUESTER),
	AUT_0401 ("AUT-0401", "Cookie Signature does not correspond", StatusCode.REQUESTER),
	AUT_0402 ("AUT-0402", "Token not found", StatusCode.REQUESTER),
	
	// Soap ValidateUserSignature
	FAT_0452 ("FAT-0452", "InvalidKeyException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0453 ("FAT-0453", "SignatureException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0454 ("FAT-0454", "NoSuchAlgorithmException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0455 ("FAT-0455", "Cookie Signature does not correspond", StatusCode.REQUESTER),
	FAT_0456 ("FAT-0456", "Token not found", StatusCode.RESPONDER),
	
	// Logout ValidateUserSignature
	FAT_0482 ("FAT-0482", "InvalidKeyException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0483 ("FAT-0483", "SignatureException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0484 ("FAT-0484", "NoSuchAlgorithmException occured during Signature verification", StatusCode.RESPONDER),
	FAT_0485 ("FAT-0485", "Cookie Signature does not correspond", StatusCode.REQUESTER),
	FAT_0486 ("FAT-0486", "Token not found", StatusCode.RESPONDER),
	
	// Http ValidateOrganizationAccess
	FAT_0502 ("FAT-0502", "Unknown Application", StatusCode.RESOURCE_NOT_RECOGNIZED),
	FAT_0503 ("FAT-0503", "Unknown AssertionConsumerServiceURL", StatusCode.RESOURCE_NOT_RECOGNIZED),
	ACC_0501 ("ACC-0501", "Access is disabled for Organization", StatusCode.REQUESTER),
	
	// Soap ValidateOrganizationAccess
	FAT_0552 ("FAT-0552", "Unknown Application", StatusCode.RESOURCE_NOT_RECOGNIZED),
	
	// Logout ValidateOrganizationAccess
	FAT_0582 ("FAT-0582", "Unknown Application", StatusCode.RESOURCE_NOT_RECOGNIZED),
	
	// Http ValidateUserAccess
	FAT_0601 ("FAT-0601", NameIDType.PERSISTENT + " requested but Federation not defined for User", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0602 ("FAT-0602", NameIDType.PERSISTENT + " requested but Federation not defined for Organization", StatusCode.INVALID_NAMEID_POLICY),
	ACC_0601 ("ACC-0601", "Access is disabled for User", StatusCode.REQUESTER),
	ACC_0602 ("ACC-0602", "Access not found for User", StatusCode.REQUESTER),
	ACC_0603 ("ACC-0603", "Cannot create Session for User", StatusCode.RESPONDER),
	
	// Soap ValidateUserAccess
	FAT_0651 ("FAT-0651", NameIDType.PERSISTENT + " requested but Federation not defined for User", StatusCode.INVALID_NAMEID_POLICY),
	ACC_0651 ("ACC-0651", "Access is disabled for User", StatusCode.REQUESTER),
	ACC_0652 ("ACC-0652", "Access not found for User", StatusCode.REQUESTER),
	
	// Logout ValidateUserAccess
	FAT_0681 ("FAT-0681", NameIDType.PERSISTENT + " requested but Federation not defined for User", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0682 ("FAT-0682", "NameID does not correspond to current logged in User", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0683 ("FAT-0683", "Entity does not correspond to current Application", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0684 ("FAT-0684", "NameID does not correspond to current logged in User'd ID", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0685 ("FAT-0685", "Federation not enabled for Organization", StatusCode.INVALID_NAMEID_POLICY),
	FAT_0686 ("FAT-0686", "NameID does not correspond to current logged in User's Federation ID", StatusCode.INVALID_NAMEID_POLICY),
	ACC_0681 ("ACC-0681", "Access is disabled for User", StatusCode.REQUESTER),
	ACC_0682 ("ACC-0682", "Access not found for User", StatusCode.REQUESTER),
	
	// SingleSignOnFilter
	FAT_0701 ("FAT-0701", "Authentication phase not correctly set", StatusCode.RESPONDER),
	
	// SAMLUtils
	FAT_0801 ("FAT-0801", "Unable to generate XML AuthnRequest from Request", StatusCode.RESPONDER),
	FAT_0802 ("FAT-0802", "Unable to generate AuthnRequest from Request", StatusCode.RESPONDER),
	FAT_0803 ("FAT-0803", "MarshallingException occured during Assertion signature", StatusCode.RESPONDER),
	FAT_0804 ("FAT-0804", "SignatureException occured during Assertion signature", StatusCode.RESPONDER),
	FAT_0805 ("FAT-0805", "Cannot handle Organization Certificate", StatusCode.RESPONDER),
	
	// AuthenticateProvider
	FAT_0901 ("FAT-0901", "Authentication phase not correctly set", StatusCode.RESPONDER),
	FAT_0902 ("FAT-0902", "Mismatch during authentication", StatusCode.UNKNOWN_PRINCIPAL),
	FAT_0903 ("FAT-0903", "Unable to decrypt the password", StatusCode.REQUESTER),
	CRE_0901 ("CRE-0901", "Wrong password", StatusCode.REQUESTER),
	ACC_0901 ("ACC-0901", "Access is disabled for User", StatusCode.REQUESTER),
	ACC_0902 ("ACC-0902", "Access not found for User", StatusCode.REQUESTER),
	ACC_0903 ("ACC-0903", "Cannot create Session for User", StatusCode.RESPONDER),
	
	// LoginAuthenticateFilter
	FAT_1001 ("FAT-1001", "IDP Initialization not supported", StatusCode.REQUESTER),
	
	// FinishAuthenticateFilter
	FAT_1101 ("FAT-1101", "Expected Headers 'username' and 'organization' not found", StatusCode.REQUEST_DENIED),
	FAT_1102 ("FAT-1102", "Cannot create Session for User", StatusCode.RESPONDER),
	FAT_1103 ("FAT-1103", "Authentication phase not correctly set", StatusCode.REQUESTER),
	FAT_1104 ("FAT-1104", "Unexpected error during JSON Desrialization", StatusCode.RESOURCE_NOT_RECOGNIZED),

	// ValidateRequestURLStage
	FAT_1201 ("FAT-1201", "Authentication phase not correctly set", StatusCode.RESPONDER),
	FAT_1202 ("FAT-1202", "AuthnRequest entity not found", StatusCode.RESPONDER),
	FAT_1203 ("FAT-1203", "Only " + SAMLConstants.SAML2_REDIRECT_BINDING_URI + " and " + SAMLConstants.SAML2_ARTIFACT_BINDING_URI + " accepted for GET request", StatusCode.UNSUPPORTED_BINDING),
	FAT_1204 ("FAT-1204", "Only SAMLRequest, SigAlg, Signature, RelayState parameters accepted for " + SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI + " binding", StatusCode.UNSUPPORTED_BINDING),
	FAT_1205 ("FAT-1205", "MalformedURLException occured during URL validation", StatusCode.INVALID_ATTR_NAME_OR_VALUE),
	FAT_1206 ("FAT-1206", "Only " + SAMLConstants.SAML2_POST_BINDING_URI + " and " + SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI + " accepted for POST request", StatusCode.UNSUPPORTED_BINDING),
	FAT_1207 ("FAT-1207", "Only HTTPS accepted for " + AuthnContext.PPT_AUTHN_CTX + " AuthnContextClassRef", StatusCode.INVALID_ATTR_NAME_OR_VALUE),
	
	// SAMLAuthenticationHandler
	FAT_1301 ("FAT-1301", "Cannot handle SAML Binding", StatusCode.REQUEST_DENIED),
	FAT_1302 ("FAT-1302", "Unable to sign the final Token", StatusCode.RESPONDER),
	FAT_1303 ("FAT-1303", "MessageEncodingException occured during final POST operation", StatusCode.RESPONDER),
	FAT_1304 ("FAT-1304", "ComponentInitializationException occured during final POST operation", StatusCode.RESPONDER),
	FAT_1305 ("FAT-1305", "Organization / User not activated", StatusCode.REQUEST_DENIED),
	
	// SOAPAuthenticationHandler
	FAT_1401 ("FAT-1401", "MessageDecodingException occured during ArtifactResponse generation", StatusCode.REQUEST_DENIED),
	FAT_1402 ("FAT-1402", "ComponentInitializationException occured during ArtifactResponse generation", StatusCode.RESPONDER),
	
	// SOAPAuthenticationHandler
	FAT_1501 ("FAT-1501", "Cannot send SOAP LogoutRequest", StatusCode.RESPONDER),
	
	;
	private String code;
	private String reason;
	private String status;
	
	private StageResultCode(String code, String reason, String status) {
		this.code = code;
		this.reason = reason;
		this.status = status;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getReason() {
		return reason;
	}
	
	public String getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return "Code: " + getCode() + 
			", Reason: " + getReason() + 
			", SAML Status: " + getStatus();
	}

}
