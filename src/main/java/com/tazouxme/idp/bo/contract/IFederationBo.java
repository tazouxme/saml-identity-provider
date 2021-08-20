package com.tazouxme.idp.bo.contract;

import java.util.Set;

import com.tazouxme.idp.exception.FederationException;
import com.tazouxme.idp.model.Federation;

public interface IFederationBo {
	
	public Set<Federation> findByURN(String urn, String organizationExternalId);
	
	public Set<Federation> findByOrganization(String organizationExternalId);
	
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId);
	
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws FederationException;
	
	public Federation create(Federation federation) throws FederationException;
	
	public Federation update(Federation federation) throws FederationException;
	
	public void delete(Federation federation) throws FederationException;

}
