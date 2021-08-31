package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

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
	public Access findByExternalId(String externalId, String organizationExternalId) throws AccessException {
		return dao.findByExternalId(externalId, organizationExternalId);
	}
	
	@Override
	public Set<Access> findByURN(String urn, String organizationExternalId) {
		return dao.findByURN(urn, organizationExternalId);
	}

	@Override
	public Set<Access> findByOrganization(String organizationExternalId) {
		return dao.findByOrganization(organizationExternalId);
	}

	@Override
	public Set<Access> findByUser(String userExternalId, String organizationExternalId) {
		return dao.findByUser(userExternalId, organizationExternalId);
	}
	
	@Override
	public Access findByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws AccessException {
		return dao.findByUserAndURN(userExternalId, urn, organizationExternalId);
	}

	@Override
	public Access create(Access access) throws AccessException {
		access.setExternalId(IDUtils.generateId("ACC_", 8));
		access.setCreationDate(new Date().getTime());
		access.setStatus(1);
		
		return dao.create(access);
	}

	@Override
	public Access update(Access access) throws AccessException {
		Access pAccess = findByExternalId(access.getExternalId(), access.getOrganization().getExternalId());
		pAccess.setEnabled(access.isEnabled());
		
		if (Objects.nonNull(access.getRole())) {
			pAccess.setRole(access.getRole());
		}
		
		return dao.update(pAccess);
	}

	@Override
	public void delete(Access access) throws AccessException {
		dao.delete(findByExternalId(access.getExternalId(), access.getOrganization().getExternalId()));
	}

}
