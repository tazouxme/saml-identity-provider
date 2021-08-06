package com.tazouxme.idp.bo;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IClaimBo;
import com.tazouxme.idp.dao.contract.IClaimDao;
import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.model.Claim;
import com.tazouxme.idp.util.IDUtils;

public class ClaimBo implements IClaimBo {
	
	@Autowired
	private IClaimDao dao;

	@Override
	public Set<Claim> findAll(String externalOrganizationId) {
		return dao.findAll(externalOrganizationId);
	}

	@Override
	public Claim findByExternalId(String externalId, String externalOrganizationId) throws ClaimException {
		return dao.findByExternalId(externalId, externalOrganizationId);
	}
	
	@Override
	public Claim findByURI(String uri, String externalOrganizationId) throws ClaimException {
		return dao.findByURI(uri, externalOrganizationId);
	}

	@Override
	public Claim create(Claim claim) throws ClaimException {
		claim.setExternalId(IDUtils.generateId("CLA_", 8));
		return dao.create(claim);
	}

	@Override
	public Claim update(Claim claim) throws ClaimException {
		Claim pClaim = findByExternalId(claim.getExternalId(), claim.getOrganization().getExternalId());
		pClaim.setName(claim.getName());
		pClaim.setDescription(claim.getDescription());
		
		return dao.update(claim);
	}

	@Override
	public void delete(Claim claim) throws ClaimException {
		dao.delete(findByExternalId(claim.getExternalId(), claim.getOrganization().getExternalId()));
	}

}
