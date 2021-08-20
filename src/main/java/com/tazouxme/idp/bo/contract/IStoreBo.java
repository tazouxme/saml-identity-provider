package com.tazouxme.idp.bo.contract;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.StoreException;
import com.tazouxme.idp.model.Store;

public interface IStoreBo {

	@Transactional(readOnly = true)
	public Set<Store> findAll(String context, String organizationId);
	
	@Transactional(readOnly = true)
	public Store findByKey(String context, String key, String organizationId) throws StoreException;
	
	@Transactional(readOnly = true)
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId) throws StoreException;
	
	@Transactional
	public Store create(Store store) throws StoreException;
	
	@Transactional
	public Store update(Store store) throws StoreException;
	
	@Transactional
	public void delete(Store store) throws StoreException;

}
