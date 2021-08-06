package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.model.Role;

public interface IRoleDao {
	
	public Set<Role> findAll(String externalOrganizationId);
	
	public Role findByExternalId(String externalId, String externalOrganizationId) throws RoleException;
	
	public Role findByURI(String uri, String externalOrganizationId) throws RoleException;
	
	public Role create(Role role) throws RoleException;
	
	public Role update(Role role) throws RoleException;
	
	public void delete(Role role) throws RoleException;

}
