package com.tazouxme.idp.security.token;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.parameters.StageParameters;

public class UserAuthenticationDetails {
	
	private UserAuthenticationType type;
	private UserAuthenticationPhase phase;
	private StageParameters parameters;
	private StageResultCode resultCode;
	
	private UserIdentity identity = new UserIdentity();
	
	public UserAuthenticationType getType() {
		return type;
	}
	
	public void setType(UserAuthenticationType type) {
		this.type = type;
	}
	
	public UserAuthenticationPhase getPhase() {
		return phase;
	}
	
	public void setPhase(UserAuthenticationPhase phase) {
		this.phase = phase;
	}
	
	public StageParameters getParameters() {
		return parameters;
	}
	
	public void setParameters(StageParameters parameters) {
		this.parameters = parameters;
	}
	
	public StageResultCode getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(StageResultCode resultCode) {
		this.resultCode = resultCode;
	}
	
	public boolean isSuccess() {
		return StageResultCode.OK.equals(getResultCode());
	}
	
	public UserIdentity getIdentity() {
		return identity;
	}
	
	public void setIdentity(UserIdentity identity) {
		this.identity = identity;
	}

}
