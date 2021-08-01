package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;

public interface IApplicationDao {
	
	public Application findByUrn(String urn) throws ApplicationException;
	
	public Application findByExternalId(String externalId) throws ApplicationException;
	
	public Application create(Application application) throws ApplicationException;
	
	public Application update(Application application) throws ApplicationException;
	
	public void delete(Application application) throws ApplicationException;

}
