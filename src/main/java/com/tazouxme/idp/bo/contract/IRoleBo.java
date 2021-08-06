package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.model.Role;

public interface IRoleBo {

	@Transactional(readOnly = true)
	public Set<Role> findAll(String externalOrganizationId);

	@Transactional(readOnly = true)
	public Role findByExternalId(String externalId, String externalOrganizationId) throws RoleException;

	@Transactional(readOnly = true)
	public Role findByURI(String uri, String externalOrganizationId) throws RoleException;

	@Transactional
	public Role create(Role role) throws RoleException;
	
	@Transactional
	public Role update(Role role) throws RoleException;
	
	@Transactional
	public void delete(Role role) throws RoleException;

}
