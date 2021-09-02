package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.model.Organization;

public interface IOrganizationDao {
	
	public Organization findByExternalId(String externalId);
	
	public Organization findByDomain(String domain);
	
	public Organization create(Organization org);
	
	public Organization update(Organization org);
	
	public void delete(Organization org);

}
