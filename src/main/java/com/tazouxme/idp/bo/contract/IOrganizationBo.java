package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Organization;

public interface IOrganizationBo {
	
	@Transactional(readOnly = true)
	public Organization findByExternalId(String externalId);
	
	@Transactional(readOnly = true)
	public Organization findByDomain(String domain);
	
	@Transactional
	public Organization create(Organization org);
	
	@Transactional
	public Organization update(Organization org);
	
	@Transactional
	public Organization updateCertificate(Organization org);
	
	@Transactional
	public void delete(Organization org);

}
