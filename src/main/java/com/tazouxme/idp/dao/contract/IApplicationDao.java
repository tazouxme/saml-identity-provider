package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Application;

public interface IApplicationDao {
	
	public Set<Application> findAll(String organizationExternalId);
	
	public Application findByUrn(String urn, String organizationExternalId);
	
	public Application findByExternalId(String externalId, String organizationExternalId);
	
	public Application create(Application application);
	
	public Application update(Application application);
	
	public void delete(Application application);

}
