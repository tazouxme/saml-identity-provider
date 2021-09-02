package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Federation;

public interface IFederationDao {

	public Set<Federation> findByURN(String urn, String organizationExternalId);
	
	public Set<Federation> findByOrganization(String organizationExternalId);
	
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId);
	
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId);
	
	public Federation create(Federation federation);
	
	public Federation update(Federation federation);
	
	public void delete(Federation federation);

}
