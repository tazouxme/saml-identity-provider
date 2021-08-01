package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;

public interface IApplicationBo {
	
	@Transactional(readOnly = true)
	public Application findByUrn(String urn) throws ApplicationException;
	
	@Transactional(readOnly = true)
	public Application findByExternalId(String externalId) throws ApplicationException;
	
	@Transactional
	public Application create(Application application) throws ApplicationException;
	
	@Transactional
	public Application update(Application application) throws ApplicationException;
	
	@Transactional
	public void delete(Application application) throws ApplicationException;

}
