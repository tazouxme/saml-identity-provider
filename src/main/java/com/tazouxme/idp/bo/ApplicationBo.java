package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.dao.contract.IApplicationDao;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.util.IDUtils;

public class ApplicationBo implements IApplicationBo {
	
	@Autowired
	private IApplicationDao dao;
	
	@Override
	public Set<Application> findAll(String organizationExternalId) {
		return dao.findAll(organizationExternalId);
	}

	@Override
	public Application findByUrn(String urn, String organizationExternalId) {
		return dao.findByUrn(urn, organizationExternalId);
	}

	@Override
	public Application findByExternalId(String externalId, String organizationExternalId) {
		return dao.findByExternalId(externalId, organizationExternalId);
	}

	@Override
	public Application create(Application application) {
		application.setExternalId(IDUtils.generateId("APP_", 8));
		application.setCreationDate(new Date().getTime());
		application.setStatus(1);
		
		return dao.create(application);
	}

	@Override
	public Application update(Application application) {
		Application pApplication = findByExternalId(application.getExternalId(), application.getOrganization().getExternalId());
		pApplication.setName(application.getName());
		pApplication.setDescription(application.getDescription());
		pApplication.setAssertionUrl(application.getAssertionUrl());
		pApplication.setLogoutUrl(application.getLogoutUrl());
		
		return dao.update(pApplication);
	}

	@Override
	public Application updateClaims(Application application) {
		Application pApplication = findByExternalId(application.getExternalId(), application.getOrganization().getExternalId());
		if (pApplication.getClaims().size() != application.getClaims().size() || !pApplication.getClaims().containsAll(application.getClaims())) {
			pApplication.getClaims().clear();
			pApplication.getClaims().addAll(application.getClaims());
		}
		
		return dao.update(pApplication);
	}

	@Override
	public void delete(Application application) {
		dao.delete(findByExternalId(application.getExternalId(), application.getOrganization().getExternalId()));
	}

}
