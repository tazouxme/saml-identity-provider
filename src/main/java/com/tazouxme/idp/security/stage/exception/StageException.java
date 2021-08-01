package com.tazouxme.idp.security.stage.exception;

import org.springframework.security.core.AuthenticationException;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.parameters.StageParameters;

public class StageException extends AuthenticationException {

	private StageExceptionType type;
	private StageResultCode code;
	private StageParameters params;
	
	public StageException(StageExceptionType type, StageResultCode code) {
		super(code.getReason());
		this.type = type;
		this.code = code;
	}
	
	public StageException(StageExceptionType type, StageResultCode code, StageParameters params) {
		super(code.getReason());
		this.type = type;
		this.code = code;
		this.params = params;
	}
	
	public StageExceptionType getType() {
		return type;
	}

	public StageResultCode getCode() {
		return code;
	}
	
	public StageParameters getParams() {
		return params;
	}

}
