package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.model.Store;

public interface IStoreDao {
	
	public Set<Store> findAll(String context, String organizationId);
	
	public Store findByKey(String context, String key, String organizationId);
	
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId);
	
	public Store create(Store store);
	
	public Store update(Store store);
	
	public void delete(Store store);

}
