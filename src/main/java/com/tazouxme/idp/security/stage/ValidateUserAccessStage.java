package com.tazouxme.idp.security.stage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.security.token.UserIdentity;

public class ValidateUserAccessStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IAccessBo accessBo;
	
	@Autowired
	private ISessionBo sessionBo;
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (!UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0601, o);
		}
		
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
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getUser().getExternalId(), "", access.getRole().getName());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setType(UserAuthenticationType.SAML);
		authentication.getDetails().setPhase(UserAuthenticationPhase.USER_ACCESS_VALID);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		try {
			String token = registerToken(o.getOrganizationId(), o.getUserId());
			authentication.getDetails().getIdentity().setToken(token);
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0603, o);
		}
		
		logger.info("User access valid");
		
		return authentication;
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
