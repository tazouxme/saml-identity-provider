package com.tazouxme.idp.security.stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateOrganizationAccessStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IApplicationBo applicationBo;
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (!UserAuthenticationPhase.SIGNATURES_VALID.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0501, o);
		}
		
		try {
			Application application = applicationBo.findByUrn(o.getAuthnRequest().getIssuer().getValue(), o.getOrganizationId());
			if (!o.getAuthnRequest().getAssertionConsumerServiceURL().equals(application.getAssertionUrl())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0503, o);
			}
			
			o.setApplication(application);
		} catch (ApplicationException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0502, o);
		}
		
		logger.info("Organization access valid");
		
		Organization organization = o.getOrganization();
		authentication.getDetails().getIdentity().setOrganizationId(organization.getExternalId());
		authentication.getDetails().getIdentity().setOrganization(organization.getCode());
		authentication.getDetails().setPhase(UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID);
		
		return authentication;
	}

}
