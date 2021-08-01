package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.model.Session;

public interface ISessionDao {
	
	public Session find(String orgExternalId, String userExternalId) throws SessionException;
	
	public Session create(Session session) throws SessionException;
	
	public void delete(Session session) throws SessionException;

}
