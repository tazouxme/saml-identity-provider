package com.tazouxme.idp.security.stage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Base64;

import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.util.SAMLUtils;

public class ValidateRequestParametersStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (StringUtils.isEmpty(o.getSamlRequestParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0101, o);
		}
		
		if (StringUtils.isEmpty(o.getRelayStateParam())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0102, o);
		}
		
		logger.info("Request parameters valid");
		
		o.setAuthnRequest(SAMLUtils.unmarshallAuthnRequest(Base64.decode(o.getSamlRequestParam())));
		
		authentication.getDetails().setPhase(UserAuthenticationPhase.REQUEST_PARAMETERS_VALID);
		authentication.getDetails().setParameters(o);
		
		return authentication;
	}

}
