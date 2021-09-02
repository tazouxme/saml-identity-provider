package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.model.Session;

public interface ISessionDao {
	
	public Session find(String orgExternalId, String userExternalId);
	
	public Session create(Session session);
	
	public void delete(Session session);

}
