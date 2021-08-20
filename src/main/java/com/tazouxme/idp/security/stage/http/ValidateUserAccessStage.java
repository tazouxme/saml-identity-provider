package com.tazouxme.idp.security.stage.http;

import org.opensaml.saml.saml2.core.NameID;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.IFederationBo;
import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.FederationException;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.stage.AbstractStage;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.security.token.UserIdentity;

public class ValidateUserAccessStage extends AbstractStage {
	
	public ValidateUserAccessStage() {
		super(UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID, UserAuthenticationPhase.USER_ACCESS_VALID);
	}

	@Autowired
	private IAccessBo accessBo;
	
	@Autowired
	private ISessionBo sessionBo;
	
	@Autowired
	private IFederationBo federationBo;
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		// find user and check SaaS access
		Access access = null;
		try {
			access = accessBo.findByUserAndURN(o.getUserId(), o.getAuthnRequest().getIssuer().getValue(), o.getOrganizationId());
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
			try {
				Federation federation = federationBo.findByUserAndURN(
						user.getExternalId(), o.getApplication().getUrn(), authentication.getDetails().getIdentity().getOrganizationId());
				authentication.getDetails().getIdentity().setFederatedUserId(federation.getExternalId());
			} catch (FederationException e) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0602, o);
			}
		}
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getUser().getExternalId(), "", access.getRole().getName());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setType(UserAuthenticationType.SAML);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		try {
			String token = registerToken(o.getOrganizationId(), o.getUserId());
			authentication.getDetails().getIdentity().setToken(token);
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_0601, o);
		}
		
		logger.info("User access valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}
	
	protected String registerToken(String organizationId, String userId) throws SessionException {
		// register new token
		Session session = new Session();
		session.setOrganizationExternalId(organizationId);
		session.setUserExternalId(userId);
		
		try {
			return sessionBo.create(session).getToken();
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.FAT_1102);
		}
	}

}
