package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IFederationBo;
import com.tazouxme.idp.dao.contract.IFederationDao;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.util.IDUtils;

public class FederationBo implements IFederationBo {
	
	@Autowired
	private IFederationDao dao;

	@Override
	public Set<Federation> findByURN(String urn, String organizationExternalId) {
		return dao.findByURN(urn, organizationExternalId);
	}

	@Override
	public Set<Federation> findByOrganization(String organizationExternalId) {
		return dao.findByOrganization(organizationExternalId);
	}

	@Override
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId) {
		return dao.findByUser(userExternalId, organizationExternalId);
	}

	@Override
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId) {
		return dao.findByUserAndURN(userExternalId, urn, organizationExternalId);
	}

	@Override
	public Federation create(Federation federation) {
		federation.setExternalId(IDUtils.generateId("FED_", 8));
		federation.setCreationDate(new Date().getTime());
		federation.setStatus(1);
		
		return dao.create(federation);
	}

	@Override
	public Federation update(Federation federation) {
		Federation pFed = findByUserAndURN(federation.getUser().getExternalId(), federation.getApplication().getUrn(), federation.getOrganization().getExternalId());
		pFed.setEnabled(federation.isEnabled());
		
		return dao.update(pFed);
	}

	@Override
	public void delete(Federation federation) {
		dao.delete(findByUserAndURN(federation.getUser().getExternalId(), federation.getApplication().getUrn(), federation.getOrganization().getExternalId()));
	}

}
