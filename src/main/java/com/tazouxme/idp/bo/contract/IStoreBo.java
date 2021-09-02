package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.Store;

public interface IStoreBo {

	@Transactional(readOnly = true)
	public Set<Store> findAll(String context, String organizationId);
	
	@Transactional(readOnly = true)
	public Store findByKey(String context, String key, String organizationId);
	
	@Transactional(readOnly = true)
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId);
	
	@Transactional
	public Store create(Store store);
	
	@Transactional
	public Store update(Store store);
	
	@Transactional
	public void delete(Store store);

}
