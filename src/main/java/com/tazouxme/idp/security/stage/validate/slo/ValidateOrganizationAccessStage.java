package com.tazouxme.idp.security.stage.validate.slo;

import com.tazouxme.idp.application.exception.ApplicationException;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateOrganizationAccessStage extends AbstractStage {

	public ValidateOrganizationAccessStage() {
		super(UserAuthenticationPhase.SIGNATURES_VALID, UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		try {
			o.setApplication(idpApplication.findApplicationByURN(o.getLogoutRequest().getIssuer().getValue(), o.getOrganizationId()));
		} catch (ApplicationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0582, o);
		}
		
		logger.info("Organization access valid");
		
		Organization organization = o.getOrganization();
		authentication.getDetails().getIdentity().setOrganizationId(organization.getExternalId());
		authentication.getDetails().getIdentity().setOrganization(organization.getCode());
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
