package com.tazouxme.idp.dao.contract;

import java.util.Set;

import com.tazouxme.idp.exception.StoreException;
import com.tazouxme.idp.model.Store;

public interface IStoreDao {
	
	public Set<Store> findAll(String context, String organizationId);
	
	public Store findByKey(String context, String key, String organizationId) throws StoreException;
	
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId) throws StoreException;
	
	public Store create(Store store) throws StoreException;
	
	public Store update(Store store) throws StoreException;
	
	public void delete(Store store) throws StoreException;

}
