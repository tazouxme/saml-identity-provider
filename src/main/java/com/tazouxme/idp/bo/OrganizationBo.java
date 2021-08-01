package com.tazouxme.idp.bo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.dao.contract.IOrganizationDao;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.util.IDUtils;

public class OrganizationBo implements IOrganizationBo {
	
	@Autowired
	private IOrganizationDao dao;

	@Override
	public Organization findByExternalId(String externalId) throws OrganizationException {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Organization findByDomain(String domain) throws OrganizationException {
		return dao.findByDomain(domain);
	}

	@Override
	public Organization create(Organization org) throws OrganizationException {
		org.setExternalId(IDUtils.generateId("ORG_", 8));
		org.setCreationDate(new Date().getTime());
		return dao.create(org);
	}

	@Override
	public Organization update(Organization org) throws OrganizationException {
		Organization pOrg = findByExternalId(org.getExternalId());
		pOrg.setEnabled(org.isEnabled());
		pOrg.setName(org.getName());
		pOrg.setDescription(org.getDescription());
		pOrg.setPublicKey(org.getPublicKey());
		
		return dao.update(org);
	}

	@Override
	public void delete(Organization org) throws OrganizationException {
		dao.delete(findByExternalId(org.getExternalId()));
	}

}
