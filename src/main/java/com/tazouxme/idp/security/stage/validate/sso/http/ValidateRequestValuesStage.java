package com.tazouxme.idp.security.stage.validate.sso.http;

import java.time.Instant;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.NameIDType;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateRequestValuesStage extends AbstractStage {

	public ValidateRequestValuesStage() {
		super(UserAuthenticationPhase.REQUEST_PARAMETERS_VALID, UserAuthenticationPhase.REQUEST_VALUES_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		if (StringUtils.isEmpty(o.getAuthnRequest().getID())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0203, o);
		}
		if (StringUtils.isEmpty(o.getAuthnRequest().getAssertionConsumerServiceURL())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0204, o);
		}
		if (o.getAuthnRequest().getIssueInstant() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0205, o);
		}
		if (o.getAuthnRequest().getIssueInstant().isAfter(Instant.now())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0206, o);
		}
		if (!IdentityProviderConstants.SAML_VERSION.equals(o.getAuthnRequest().getVersion().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0207, o);
		}
		
		String protocolBinding = o.getAuthnRequest().getProtocolBinding();
		Set<String> allowedProtocolBindings = Set.of(SAMLConstants.SAML2_REDIRECT_BINDING_URI, SAMLConstants.SAML2_POST_BINDING_URI, SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI, 
				SAMLConstants.SAML2_ARTIFACT_BINDING_URI/*, SAMLConstants.SAML2_SOAP11_BINDING_URI*/);
		
		if (!allowedProtocolBindings.contains(protocolBinding)) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0208, o);
		}
		if (o.getAuthnRequest().getIssuer() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0209, o);
		}
		if (StringUtils.isEmpty(o.getAuthnRequest().getIssuer().getValue())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0210, o);
		}
		if (!o.getIdpUrn().equals(o.getAuthnRequest().getDestination())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0211, o);
		}

		if (o.getAuthnRequest().getNameIDPolicy() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0214, o);
		}
		
		Set<String> allowedNameIDPolicies = Set.of(NameIDType.UNSPECIFIED, NameIDType.EMAIL, NameIDType.TRANSIENT, NameIDType.ENCRYPTED, NameIDType.PERSISTENT, NameIDType.ENTITY);
		String nameIDPolicy = o.getAuthnRequest().getNameIDPolicy().getFormat();
		
		if (!allowedNameIDPolicies.contains(nameIDPolicy)) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0215, o);
		}
		if (Boolean.TRUE.equals(o.getAuthnRequest().getNameIDPolicy().getAllowCreate())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0216, o);
		}

		if (o.getAuthnRequest().getRequestedAuthnContext() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0217, o);
		}
		if (o.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs() == null ||
				o.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().size() != 1) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0218, o);
		}
		
		String authnContextClassRef = o.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI();
		Set<String> allowedAuthnContextClassRef = Set.of(AuthnContext.PASSWORD_AUTHN_CTX, AuthnContext.PPT_AUTHN_CTX/*, AuthnContext.TLS_CLIENT_AUTHN_CTX, AuthnContext.X509_AUTHN_CTX*/);
		
		if (!allowedAuthnContextClassRef.contains(authnContextClassRef)) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0219, o);
		}
		
		if (o.getAuthnRequest().isForceAuthn()) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0201, o);
		}
		
		logger.info("Request values valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
