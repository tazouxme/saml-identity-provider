package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IRoleBo;
import com.tazouxme.idp.dao.contract.IRoleDao;
import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.model.Role;
import com.tazouxme.idp.util.IDUtils;

public class RoleBo implements IRoleBo {
	
	@Autowired
	private IRoleDao dao;

	@Override
	public Set<Role> findAll(String externalOrganizationId) {
		return dao.findAll(externalOrganizationId);
	}

	@Override
	public Role findByExternalId(String externalId, String externalOrganizationId) throws RoleException {
		return dao.findByExternalId(externalId, externalOrganizationId);
	}

	@Override
	public Role findByURI(String uri, String externalOrganizationId) throws RoleException {
		return dao.findByURI(uri, externalOrganizationId);
	}

	@Override
	public Role create(Role role) throws RoleException {
		role.setExternalId(IDUtils.generateId("ROL_", 8));
		role.setCreationDate(new Date().getTime());
		role.setStatus(1);
		
		return dao.create(role);
	}

	@Override
	public Role update(Role role) throws RoleException {
		Role pRole = findByExternalId(role.getExternalId(), role.getOrganization().getExternalId());
		pRole.setName(role.getName());
		
		return dao.update(pRole);
	}

	@Override
	public void delete(Role role) throws RoleException {
		dao.delete(findByExternalId(role.getExternalId(), role.getOrganization().getExternalId()));
	}

}
