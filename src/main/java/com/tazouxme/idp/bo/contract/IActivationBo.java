package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Activation;

public interface IActivationBo {
	
	@Transactional(readOnly = true)
	public Activation find(String orgExternalId, String userExternalId, String step) ;
	
	@Transactional(readOnly = true)
	public Set<Activation> findByUser(String userExternalId, String organizationId);

	@Transactional(readOnly = true)
	public Activation findByExternalId(String externalId);

	@Transactional
	public Activation create(Activation activation);

	@Transactional
	public void delete(Activation activation);

}
