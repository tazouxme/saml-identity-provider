package com.tazouxme.idp.security.stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateOrganizationAccessStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IAccessBo accessBo;
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (!UserAuthenticationPhase.SIGNATURES_VALID.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0501, o);
		}
		
		// find organization and check SaaS access
		try {
			Access access = accessBo.findByOrganization(o.getOrganizationId(), o.getAuthnRequest().getIssuer().getValue());
			if (!access.isEnabled()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0501, o);
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0502, o);
		}
		
		logger.info("Organization access valid");
		
		Organization organization = o.getOrganization();
		authentication.getDetails().getIdentity().setOrganizationId(organization.getExternalId());
		authentication.getDetails().getIdentity().setOrganization(organization.getCode());
		authentication.getDetails().setPhase(UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID);
		
		return authentication;
	}

}
