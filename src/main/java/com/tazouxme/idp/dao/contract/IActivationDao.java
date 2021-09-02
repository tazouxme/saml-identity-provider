package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Activation;

public interface IActivationDao {
	
	public Activation find(String orgExternalId, String userExternalId, String step);
	
	public Set<Activation> findByUser(String userExternalId, String organizationId);
	
	public Activation findByExternalId(String externalId);
	
	public Activation create(Activation activation);
	
	public void delete(Activation activation);

}
