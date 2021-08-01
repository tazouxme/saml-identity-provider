package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.model.Organization;

public interface IOrganizationDao {
	
	public Organization findByExternalId(String externalId) throws OrganizationException;
	
	public Organization findByDomain(String domain) throws OrganizationException;
	
	public Organization create(Organization org) throws OrganizationException;
	
	public Organization update(Organization org) throws OrganizationException;
	
	public void delete(Organization org) throws OrganizationException;

}
