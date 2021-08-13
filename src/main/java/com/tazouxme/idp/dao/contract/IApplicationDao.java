package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;

public interface IApplicationDao {
	
	public Set<Application> findAll(String organizationExternalId);
	
	public Application findByUrn(String urn, String organizationExternalId) throws ApplicationException;
	
	public Application findByExternalId(String externalId, String organizationExternalId) throws ApplicationException;
	
	public Application create(Application application) throws ApplicationException;
	
	public Application update(Application application) throws ApplicationException;
	
	public void delete(Application application) throws ApplicationException;

}
