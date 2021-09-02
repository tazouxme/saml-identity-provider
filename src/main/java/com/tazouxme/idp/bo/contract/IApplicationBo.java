package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Application;

public interface IApplicationBo {
	
	@Transactional(readOnly = true)
	public Set<Application> findAll(String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Application findByUrn(String urn, String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Application findByExternalId(String externalId, String organizationExternalId);
	
	@Transactional
	public Application create(Application application);
	
	@Transactional
	public Application update(Application application);
	
	@Transactional
	public Application updateClaims(Application application);
	
	@Transactional
	public void delete(Application application);

}
