package com.tazouxme.idp.bo;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.dao.contract.IApplicationDao;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.util.IDUtils;

public class ApplicationBo implements IApplicationBo {
	
	@Autowired
	private IApplicationDao dao;

	@Override
	public Application findByUrn(String urn) throws ApplicationException {
		return dao.findByUrn(urn);
	}

	@Override
	public Application findByExternalId(String externalId) throws ApplicationException {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Application create(Application application) throws ApplicationException {
		application.setExternalId(IDUtils.generateId("APP_", 8));
		return dao.create(application);
	}

	@Override
	public Application update(Application application) throws ApplicationException {
		Application pApplication = findByExternalId(application.getExternalId());
		pApplication.setName(application.getName());
		pApplication.setDescription(application.getDescription());
		pApplication.setAssertionUrl(application.getAssertionUrl());
		
		return dao.update(pApplication);
	}

	@Override
	public void delete(Application application) throws ApplicationException {
		dao.delete(findByExternalId(application.getExternalId()));
	}

}
