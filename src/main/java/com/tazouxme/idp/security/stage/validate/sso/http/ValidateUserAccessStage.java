package com.tazouxme.idp.security.stage.validate.sso.http;

import org.opensaml.saml.saml2.core.NameID;

import com.tazouxme.idp.application.exception.AccessException;
import com.tazouxme.idp.application.exception.FederationException;
import com.tazouxme.idp.application.exception.SessionException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.model.Organization;
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
			access = idpApplication.findAccessByUserAndURN(o.getUserId(), o.getAuthnRequest().getIssuer().getValue(), o.getOrganizationId());
			if (!access.isEnabled()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0601, o);
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0602, o);
		}
		
		User user = o.getUser();
		authentication.getDetails().getIdentity().setUserId(user.getExternalId());
		authentication.getDetails().getIdentity().setEmail(user.getEmail());
		authentication.getDetails().getIdentity().setRole(user.isAdministrator() ? "ADMIN" : "USER");
		
		if (NameID.PERSISTENT.equals(o.getAuthnRequest().getNameIDPolicy().getFormat())) {
			if (!o.getOrganization().isFederation()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_0602, o);
			}
			
			try {
				Federation federation = idpApplication.findFederationByUserAndURN(
						user.getExternalId(), o.getApplication().getUrn(), authentication.getDetails().getIdentity().getOrganizationId());
				authentication.getDetails().getIdentity().setFederatedUserId(federation.getExternalId());
			} catch (FederationException e) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_0601, o);
			}
		}
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getUser().getExternalId(), "", access.getRole().getName());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setType(UserAuthenticationType.SAML);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		try {
			String token = registerToken(o.getUser(), o.getOrganization());
			authentication.getDetails().getIdentity().setToken(token);
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0603, o);
		}
		
		logger.info("User access valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}
	
	protected String registerToken(User user, Organization organization) throws SessionException {
		try {
			return idpApplication.createSession(user, organization, user.getExternalId()).getToken();
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_1102);
		}
	}

}
