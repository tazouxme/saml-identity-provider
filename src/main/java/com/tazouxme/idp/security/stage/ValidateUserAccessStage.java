package com.tazouxme.idp.security.stage;

import java.util.UUID;

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
			access = accessBo.findByUser(o.getUserId(), o.getAuthnRequest().getIssuer().getValue());
			if (!access.isEnabled()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0601, o);
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0602, o);
		}
		
		User user = o.getUser();
		authentication.getDetails().getIdentity().setUserId(user.getExternalId());
		authentication.getDetails().getIdentity().setEmail(user.getEmail());
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getAccessKey(), "", access.getRole());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setPhase(UserAuthenticationPhase.USER_ACCESS_VALID);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		Session session = new Session();
		session.setOrganizationExternalId(o.getOrganizationId());
		session.setUserExternalId(o.getUserId());
		session.setToken(UUID.randomUUID().toString());
		
		try {
			sessionBo.create(session);
			authentication.getDetails().getIdentity().setToken(session.getToken());
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0603, o);
		}
		
		logger.info("User access valid");
		
		return authentication;
	}

}
