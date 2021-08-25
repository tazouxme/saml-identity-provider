package com.tazouxme.idp.security.stage.validate.sso.http;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContext;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.net.URLBuilder;

public class ValidateRequestURLStage extends AbstractStage {

	public ValidateRequestURLStage() {
		super(UserAuthenticationPhase.REQUEST_VALUES_VALID, UserAuthenticationPhase.REQUEST_URL_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		if (o.getAuthnRequest() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1202, o);
		}
		
		String protocolBinding = o.getAuthnRequest().getProtocolBinding();
		if ("GET".equals(o.getUrlMethod()) && 
				(!SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(protocolBinding) && !SAMLConstants.SAML2_ARTIFACT_BINDING_URI.equals(protocolBinding))) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1203, o);
		}
		if ("POST".equals(o.getUrlMethod())) {
			if (SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(protocolBinding)) {
				Set<String> allowedParameters = Set.of("SAMLRequest", "SigAlg", "Signature", "RelayState");
				
				try {
					URLBuilder urlBuilder = new URLBuilder(o.getUrlParam());
					List<Pair<String, String>> params = urlBuilder.getQueryParams();
				
					for (Pair<String, String> param : params) {
						if (!allowedParameters.contains(param.getFirst())) {
							throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1204, o);
						}
					}
				} catch (MalformedURLException e) {
					throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1205, o);
				}
			}
			
			if (!SAMLConstants.SAML2_POST_BINDING_URI.equals(protocolBinding)) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1206, o);
			}
		}
		
		String authnContextClassRef = o.getAuthnRequest().getRequestedAuthnContext().getAuthnContextClassRefs().get(0).getURI();
		if (AuthnContext.PPT_AUTHN_CTX.equals(authnContextClassRef) && !o.getUrlParam().startsWith("https")) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1207, o);
		}
		
		logger.info("Request URL valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
