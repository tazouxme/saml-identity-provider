package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Claim;

public interface IClaimDao {
	
	public Set<Claim> findAll(String externalOrganizationId);
	
	public Claim findByExternalId(String externalId, String externalOrganizationId);
	
	public Claim findByURI(String uri, String externalOrganizationId);
	
	public Claim create(Claim claim);
	
	public Claim update(Claim claim);
	
	public void delete(Claim claim);

}
