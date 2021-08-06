package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.model.Claim;

public interface IClaimBo {

	@Transactional(readOnly = true)
	public Set<Claim> findAll(String externalOrganizationId);

	@Transactional(readOnly = true)
	public Claim findByExternalId(String externalId, String externalOrganizationId) throws ClaimException;
	
	@Transactional(readOnly = true)
	public Claim findByURI(String uri, String externalOrganizationId) throws ClaimException;

	@Transactional
	public Claim create(Claim claim) throws ClaimException;
	
	@Transactional
	public Claim update(Claim claim) throws ClaimException;
	
	@Transactional
	public void delete(Claim claim) throws ClaimException;

}
