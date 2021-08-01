package com.tazouxme.idp.security.stage;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.NameIDType;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateRequestValuesStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IApplicationBo bo;
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (!UserAuthenticationPhase.REQUEST_PARAMETERS_VALID.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0201, o);
		}
		
		if (StringUtils.isEmpty(o.getAuthnRequest().getID())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0203, o);
		}
		if (StringUtils.isEmpty(o.getAuthnRequest().getAssertionConsumerServiceURL())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0204, o);
		}
		if (StringUtils.isEmpty(o.getAuthnRequest().getIssueInstant().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0205, o);
		}
		if (o.getAuthnRequest().getIssueInstant().getEpochSecond() > new Date().getTime()) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0206, o);
		}
		if (!IdentityProviderConstants.SAML_VERSION.equals(o.getAuthnRequest().getVersion().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0207, o);
		}
		
		String protocolBinding = o.getAuthnRequest().getProtocolBinding();
		Set<String> allowedProtocolBindings = Set.of(SAMLConstants.SAML2_REDIRECT_BINDING_URI, SAMLConstants.SAML2_POST_BINDING_URI, SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI);
		
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
		
		try {
			Application application = bo.findByUrn(o.getAuthnRequest().getIssuer().getValue());
			if (!application.getAssertionUrl().equals(o.getAuthnRequest().getAssertionConsumerServiceURL())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0213, o);
			}
		} catch (ApplicationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0212, o);
		}

		if (o.getAuthnRequest().getNameIDPolicy() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0214, o);
		}
		
		Set<String> allowedNameIDPolicies = Set.of(NameIDType.EMAIL, NameIDType.PERSISTENT);
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
		Set<String> allowedAuthnContextClassRef = Set.of(AuthnContext.PASSWORD_AUTHN_CTX, AuthnContext.PPT_AUTHN_CTX);
		
		if (!allowedAuthnContextClassRef.contains(authnContextClassRef)) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0219, o);
		}
		
		if (o.getAuthnRequest().isForceAuthn()) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0201, o);
		}
		
		logger.info("Request values valid");
		
		authentication.getDetails().setPhase(UserAuthenticationPhase.REQUEST_VALUES_VALID);
		return authentication;
	}

}
