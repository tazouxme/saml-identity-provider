package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.model.Claim;

public interface IClaimDao {
	
	public Set<Claim> findAll(String externalOrganizationId);
	
	public Claim findByExternalId(String externalId, String externalOrganizationId) throws ClaimException;
	
	public Claim findByURI(String uri, String externalOrganizationId) throws ClaimException;
	
	public Claim create(Claim claim) throws ClaimException;
	
	public Claim update(Claim claim) throws ClaimException;
	
	public void delete(Claim claim) throws ClaimException;

}
