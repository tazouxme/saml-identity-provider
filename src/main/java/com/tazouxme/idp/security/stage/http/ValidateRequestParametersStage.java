package com.tazouxme.idp.security.stage.http;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;

import com.tazouxme.idp.security.stage.AbstractStage;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
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
		authentication.getDetails().setType(UserAuthenticationType.SAML);
		
		if (StringUtils.isEmpty(o.getSamlRequestParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0101, o);
		}
		if (StringUtils.isEmpty(o.getRelayStateParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0102, o);
		}
		
		if ("GET".equals(o.getUrlMethod())) {
			o.setAuthnRequest(SAMLUtils.unmarshallAuthnRequest(Base64.decode(o.getSamlRequestParam()), true));
		} else if ("POST".equals(o.getUrlMethod())) {
			o.setAuthnRequest(SAMLUtils.unmarshallAuthnRequest(Base64.decode(o.getSamlRequestParam()), false));
		} else {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0103, o);
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
