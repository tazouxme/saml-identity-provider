package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.model.Organization;

public interface IOrganizationBo {
	
	@Transactional(readOnly = true)
	public Organization findByExternalId(String externalId) throws OrganizationException;
	
	@Transactional(readOnly = true)
	public Organization findByDomain(String domain) throws OrganizationException;
	
	@Transactional
	public Organization create(Organization org) throws OrganizationException;
	
	@Transactional
	public Organization update(Organization org) throws OrganizationException;
	
	@Transactional
	public void delete(Organization org) throws OrganizationException;

}
