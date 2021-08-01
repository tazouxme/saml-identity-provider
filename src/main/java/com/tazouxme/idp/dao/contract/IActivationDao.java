package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.model.Activation;

public interface IActivationDao {
	
	public Activation find(String orgExternalId, String userExternalId, String step) throws ActivationException;
	
	public Activation findByExternalId(String externalId) throws ActivationException;
	
	public Activation create(Activation activation) throws ActivationException;
	
	public void delete(Activation activation) throws ActivationException;

}
