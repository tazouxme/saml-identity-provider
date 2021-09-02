package com.tazouxme.idp.security.stage.validate.sso.soap;

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
		super(UserAuthenticationPhase.REQUEST_VALUES_VALID, UserAuthenticationPhase.COOKIES_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		if (StringUtils.isEmpty(o.getOrganizationId())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0353, o);
		}
		if (StringUtils.isEmpty(o.getUserId())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0354, o);
		}
		if (StringUtils.isEmpty(o.getSignature())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0355, o);
		}
		
		try {
			User user = idpApplication.findUserByExternalId(o.getUserId(), o.getOrganizationId());
			o.setOrganization(user.getOrganization());
			o.setUser(user);
			
			if (!user.getOrganization().isEnabled()) {
				throw new StageException(StageExceptionType.ACTIVATION, StageResultCode.ACT_0351, o);
			}
			if (!user.isEnabled()) {
				throw new StageException(StageExceptionType.ACTIVATION, StageResultCode.ACT_0352, o);
			}
		} catch (UserException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0352, o);
		}
		
		logger.info("Cookies valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
