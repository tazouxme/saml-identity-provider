package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.dao.contract.IActivationDao;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.util.IDUtils;

public class ActivationBo implements IActivationBo {
	
	@Autowired
	private IActivationDao dao;
	
	@Override
	public Activation find(String orgExternalId, String userExternalId, String step) {
		return dao.find(orgExternalId, userExternalId, step);
	}
	
	@Override
	public Set<Activation> findByUser(String userExternalId, String organizationId) {
		return dao.findByUser(userExternalId, organizationId);
	}

	@Override
	public Activation findByExternalId(String externalId) {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Activation create(Activation activation) {
		activation.setExternalId(IDUtils.generateId("ACT_", 8));
		activation.setCreationDate(new Date().getTime());
		activation.setStatus(1);
		
		return dao.create(activation);
	}

	@Override
	public void delete(Activation activation) {
		dao.delete(findByExternalId(activation.getExternalId()));
	}

}
