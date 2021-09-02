package com.tazouxme.idp.security.stage.validate.sso.http;

import org.apache.commons.lang3.StringUtils;

import com.tazouxme.idp.application.exception.UserException;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateCookiesStage extends AbstractStage {

	public ValidateCookiesStage() {
		super(UserAuthenticationPhase.REQUEST_URL_VALID, UserAuthenticationPhase.COOKIES_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		if (StringUtils.isEmpty(o.getOrganizationId())) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0301, o);
		}
		if (StringUtils.isEmpty(o.getUserId())) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0302, o);
		}
		if (StringUtils.isEmpty(o.getSignature())) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0303, o);
		}
		
		try {
			User user = idpApplication.findUserByExternalId(o.getUserId(), o.getOrganizationId());
			o.setOrganization(user.getOrganization());
			o.setUser(user);
			
			if (!user.getOrganization().isEnabled()) {
				throw new StageException(StageExceptionType.ACTIVATION, StageResultCode.ACT_0301, o);
			}
			if (!user.isEnabled()) {
				throw new StageException(StageExceptionType.ACTIVATION, StageResultCode.ACT_0302, o);
			}
		} catch (UserException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0302, o);
		}
		
		logger.info("Cookies valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
