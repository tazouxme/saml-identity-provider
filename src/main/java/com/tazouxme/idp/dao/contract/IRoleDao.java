package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Role;

public interface IRoleDao {
	
	public Set<Role> findAll(String externalOrganizationId);
	
	public Role findByExternalId(String externalId, String externalOrganizationId);
	
	public Role findByURI(String uri, String externalOrganizationId);
	
	public Role create(Role role);
	
	public Role update(Role role);
	
	public void delete(Role role);

}
