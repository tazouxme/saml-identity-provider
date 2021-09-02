package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Access;

public interface IAccessDao {
	
	public Access findByExternalId(String externalId, String organizationExternalId);
	
	public Set<Access> findByURN(String urn, String organizationExternalId);
	
	public Set<Access> findByOrganization(String organizationExternalId);
	
	public Set<Access> findByUser(String userExternalId, String organizationExternalId);
	
	public Access findByUserAndURN(String userExternalId, String urn, String organizationExternalId);
	
	public Access create(Access access);
	
	public Access update(Access access);
	
	public void delete(Access access);

}
