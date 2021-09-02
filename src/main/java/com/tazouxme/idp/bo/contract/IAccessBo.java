package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Access;

public interface IAccessBo {
	
	@Transactional(readOnly = true)
	public Access findByExternalId(String externalId, String organizationExternalId) ;
	
	@Transactional(readOnly = true)
	public Set<Access> findByURN(String urn, String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Set<Access> findByOrganization(String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Set<Access> findByUser(String userExternalId, String organizationExternalId);
	
	@Transactional(readOnly = true)
	public Access findByUserAndURN(String userExternalId, String urn, String organizationExternalId);
	
	@Transactional
	public Access create(Access access);
	
	@Transactional
	public Access update(Access access);
	
	@Transactional
	public void delete(Access access);

}
