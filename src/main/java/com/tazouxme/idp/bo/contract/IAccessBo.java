package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.model.Access;

public interface IAccessBo {
	
	@Transactional(readOnly = true)
	public Access findByExternalId(String externalId) throws AccessException;
	
	@Transactional(readOnly = true)
	public Access findByOrganization(String organizationExternalId, String urn) throws AccessException;
	
	@Transactional(readOnly = true)
	public Access findByUser(String userExternalId, String urn) throws AccessException;
	
	@Transactional
	public Access create(Access access) throws AccessException;
	
	@Transactional
	public Access update(Access access) throws AccessException;
	
	@Transactional
	public void delete(Access access) throws AccessException;

}
