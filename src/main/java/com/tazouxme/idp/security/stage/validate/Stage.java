package com.tazouxme.idp.security.stage.validate;

import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public interface Stage {
	
	public UserAuthenticationToken execute(UserAuthenticationToken authentication,  StageParameters o) throws StageException;

}
