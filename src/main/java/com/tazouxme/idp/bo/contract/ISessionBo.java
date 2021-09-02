package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Session;

public interface ISessionBo {
	
	@Transactional(readOnly = true)
	public Session find(String orgExternalId, String userExternalId);
	
	@Transactional
	public Session create(Session session);
	
	@Transactional
	public void delete(Session session);

}
