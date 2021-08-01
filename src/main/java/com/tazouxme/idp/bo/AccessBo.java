package com.tazouxme.idp.bo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.dao.contract.IAccessDao;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.util.IDUtils;

public class AccessBo implements IAccessBo {
	
	@Autowired
	private IAccessDao dao;
	
	@Override
	public Access findByExternalId(String externalId) throws AccessException {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Access findByOrganization(String organizationExternalId, String urn) throws AccessException {
		return dao.findByOrganization(organizationExternalId, urn);
	}

	@Override
	public Access findByUser(String userExternalId, String urn) throws AccessException {
		return dao.findByUser(userExternalId, urn);
	}

	@Override
	public Access create(Access access) throws AccessException {
		access.setExternalId(IDUtils.generateId("ACC_", 8));
		access.setCreationDate(new Date().getTime());
		return dao.create(access);
	}

	@Override
	public Access update(Access access) throws AccessException {
		Access pAccess = findByExternalId(access.getExternalId());
		pAccess.setEnabled(access.isEnabled());
		pAccess.setRole(access.getRole());
		
		return dao.update(pAccess);
	}

	@Override
	public void delete(Access access) throws AccessException {
		dao.delete(findByExternalId(access.getExternalId()));
	}

}
