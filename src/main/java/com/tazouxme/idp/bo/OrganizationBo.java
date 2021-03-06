package com.tazouxme.idp.bo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.dao.contract.IOrganizationDao;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.util.IDUtils;

public class OrganizationBo implements IOrganizationBo {
	
	@Autowired
	private IOrganizationDao dao;

	@Override
	public Organization findByExternalId(String externalId) {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Organization findByDomain(String domain) {
		return dao.findByDomain(domain);
	}

	@Override
	public Organization create(Organization org) {
		org.setExternalId(IDUtils.generateId("ORG_", 8));
		org.setCreationDate(new Date().getTime());
		org.setStatus(1);
		
		return dao.create(org);
	}

	@Override
	public Organization update(Organization org) {
		Organization pOrg = findByExternalId(org.getExternalId());
		pOrg.setName(org.getName());
		
		if (!StringUtils.isBlank(org.getDescription())) {
			pOrg.setDescription(org.getDescription());
		}
		
		return dao.update(pOrg);
	}
	
	@Override
	public Organization updateCertificate(Organization org) {
		Organization pOrg = findByExternalId(org.getExternalId());
		pOrg.setCertificate(org.getCertificate());
		
		return dao.update(pOrg);
	}

	@Override
	public void delete(Organization org) {
		dao.delete(findByExternalId(org.getExternalId()));
	}

}
