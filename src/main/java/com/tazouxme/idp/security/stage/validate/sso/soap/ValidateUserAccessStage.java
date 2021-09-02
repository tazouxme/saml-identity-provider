package com.tazouxme.idp.security.stage.validate.sso.soap;

import com.tazouxme.idp.application.exception.AccessException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.security.token.UserIdentity;

public class ValidateUserAccessStage extends AbstractStage {
	
	public ValidateUserAccessStage() {
		super(UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID, UserAuthenticationPhase.USER_ACCESS_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		// find user and check SaaS access
		Access access = null;
		try {
			access = idpApplication.findAccessByUserAndURN(o.getUserId(), o.getSoapRequest().getIssuer().getValue(), o.getOrganizationId());
			if (!access.isEnabled()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0651, o);
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0652, o);
		}
		
		User user = o.getUser();
		authentication.getDetails().getIdentity().setUserId(user.getExternalId());
		authentication.getDetails().getIdentity().setEmail(user.getEmail());
		authentication.getDetails().getIdentity().setRole(user.isAdministrator() ? "ADMIN" : "USER");
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getUser().getExternalId(), "", access.getRole().getName());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setType(UserAuthenticationType.SOAP);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		logger.info("User access valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
