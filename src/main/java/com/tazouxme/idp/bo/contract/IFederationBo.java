package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Federation;

public interface IFederationBo {
	
	@Transactional(readOnly = true)
	public Set<Federation> findByURN(String urn, String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Set<Federation> findByOrganization(String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId);
	
	@Transactional
	public Federation create(Federation federation);
	
	@Transactional
	public Federation update(Federation federation);
	
	@Transactional
	public void delete(Federation federation);

}
