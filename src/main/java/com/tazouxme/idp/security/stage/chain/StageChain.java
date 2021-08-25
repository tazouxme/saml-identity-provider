package com.tazouxme.idp.security.stage.chain;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.Stage;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class StageChain {
	
	private List<Stage> stages = new LinkedList<>();
	
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		return executeInternal(authentication, o, stages.iterator());
	}
	
	private UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, 
			StageParameters o, Iterator<Stage> iterator) throws StageException {
		while (iterator.hasNext()) {
			Stage stage = iterator.next();
			authentication = stage.execute(authentication, o);
		}
		
		return authentication;
	}
	
	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}

}
