package com.tazouxme.idp.security.stage.validate.slo;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.RequestAbstractType;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.util.SAMLUtils;

public class ValidateRequestParametersStage extends AbstractStage {

	public ValidateRequestParametersStage() {
		super(null, UserAuthenticationPhase.REQUEST_PARAMETERS_VALID);
	}
	
	@Override
	protected UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		authentication.getDetails().setType(UserAuthenticationType.LOGOUT);
		
		if (StringUtils.isEmpty(o.getSamlRequestParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0181, o);
		}
		if (StringUtils.isEmpty(o.getRelayStateParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0182, o);
		}
		
		if ("GET".equals(o.getUrlMethod())) {
			RequestAbstractType authnRequest = SAMLUtils.unmarshallRequest(Base64.decode(o.getSamlRequestParam()), true);
			if (!(authnRequest instanceof LogoutRequest)) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0184, o);
			}
			
			o.setLogoutRequest((LogoutRequest) authnRequest);
		} else if ("POST".equals(o.getUrlMethod())) {
			RequestAbstractType authnRequest = SAMLUtils.unmarshallRequest(Base64.decode(o.getSamlRequestParam()), false);
			if (!(authnRequest instanceof LogoutRequest)) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0184, o);
			}
			
			o.setLogoutRequest((LogoutRequest) authnRequest);
		} else {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0183, o);
		}
		
		logger.info("Request parameters valid");
		
		authentication.getDetails().setParameters(o);
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return false;
	}

}
