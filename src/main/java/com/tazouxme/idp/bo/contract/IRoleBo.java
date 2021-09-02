package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Role;

public interface IRoleBo {

	@Transactional(readOnly = true)
	public Set<Role> findAll(String externalOrganizationId);

	@Transactional(readOnly = true)
	public Role findByExternalId(String externalId, String externalOrganizationId);

	@Transactional(readOnly = true)
	public Role findByURI(String uri, String externalOrganizationId);

	@Transactional
	public Role create(Role role);
	
	@Transactional
	public Role update(Role role);
	
	@Transactional
	public void delete(Role role);

}
