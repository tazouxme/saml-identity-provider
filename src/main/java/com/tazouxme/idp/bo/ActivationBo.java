package com.tazouxme.idp.bo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IActivationBo;
import com.tazouxme.idp.dao.contract.IActivationDao;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.model.Activation;
import com.tazouxme.idp.util.IDUtils;

public class ActivationBo implements IActivationBo {
	
	@Autowired
	private IActivationDao dao;
	
	@Override
	public Activation find(String orgExternalId, String userExternalId, String step) throws ActivationException {
		return dao.find(orgExternalId, userExternalId, step);
	}

	@Override
	public Activation findByExternalId(String externalId) throws ActivationException {
		return dao.findByExternalId(externalId);
	}

	@Override
	public Activation create(Activation activation) throws ActivationException {
		activation.setExternalId(IDUtils.generateId("ACT_", 8));
		activation.setCreationDate(new Date().getTime());
		return dao.create(activation);
	}

	@Override
	public void delete(Activation activation) throws ActivationException {
		dao.delete(findByExternalId(activation.getExternalId()));
	}

}
