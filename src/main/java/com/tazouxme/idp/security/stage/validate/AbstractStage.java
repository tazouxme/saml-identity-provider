package com.tazouxme.idp.security.stage.validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public abstract class AbstractStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	protected IIdentityProviderApplication idpApplication;
	
	private final UserAuthenticationPhase entryPhase;
	private final UserAuthenticationPhase exitPhase;
	
	public AbstractStage(UserAuthenticationPhase entryPhase, UserAuthenticationPhase exitPhase) {
		this.entryPhase = entryPhase;
		this.exitPhase = exitPhase;
	}

	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		if (requireEntryPhase() && !entryPhase.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0001, o);
		}
		
		authentication = executeInternal(authentication, o);
		
		authentication.getDetails().setPhase(exitPhase);
		return authentication;
	}
	
	protected abstract boolean requireEntryPhase();

	protected abstract UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException;

}
