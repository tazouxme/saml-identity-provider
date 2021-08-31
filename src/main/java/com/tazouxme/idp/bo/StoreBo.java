package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IStoreBo;
import com.tazouxme.idp.dao.contract.IStoreDao;
import com.tazouxme.idp.exception.StoreException;
import com.tazouxme.idp.model.Store;

public class StoreBo implements IStoreBo {
	
	@Autowired
	private IStoreDao dao;

	@Override
	public Set<Store> findAll(String context, String organizationId) {
		return dao.findAll(context, organizationId);
	}

	@Override
	public Store findByKey(String context, String key, String organizationId) throws StoreException {
		return dao.findByKey(context, key, organizationId);
	}

	@Override
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId) throws StoreException {
		return dao.findByKeyAndVersion(context, key, version, organizationId);
	}

	@Override
	public Store create(Store store) throws StoreException {
		store.setCreationDate(new Date().getTime());
		store.setStatus(1);
		
		return dao.create(store);
	}

	@Override
	public Store update(Store store) throws StoreException {
		String context = store.getContext();
		String key = store.getStoreKey();
		byte[] value = store.getStoreValue();
		Long version = store.getVersion();
		
		if (StringUtils.isBlank(context)) {
			throw new StoreException("");
		}
		
		Set<Store> stores = new HashSet<>();
		if (!StringUtils.isBlank(key)) {
			if (version != null) {
				Store pStore = findByKeyAndVersion(context, key, version, store.getOrganization().getExternalId());
				stores.add(pStore);
			} else {
				Store pStore = findByKey(context, key, store.getOrganization().getExternalId());
				stores.add(pStore);
			}
		} else {
			Set<Store> pStores = findAll(context, store.getOrganization().getExternalId());
			stores.addAll(pStores);
		}
		
		for (Store s : stores) {
			s.setExpiration(store.getExpiration());
			
			if (value != null && value.length > 0) {
				s.setStoreValue(value);
			}
			
			dao.update(s);
		}
		
		return store;
	}

	@Override
	public void delete(Store store) throws StoreException {
		String context = store.getContext();
		String key = store.getStoreKey();
		Long version = store.getVersion();
		
		if (StringUtils.isBlank(context)) {
			throw new StoreException("");
		}
		
		Set<Store> stores = new HashSet<>();
		if (!StringUtils.isBlank(key)) {
			if (version != null) {
				Store pStore = findByKeyAndVersion(context, key, version, store.getOrganization().getExternalId());
				stores.add(pStore);
			} else {
				Store pStore = findByKey(context, key, store.getOrganization().getExternalId());
				stores.add(pStore);
			}
		} else {
			Set<Store> pStores = findAll(context, store.getOrganization().getExternalId());
			stores.addAll(pStores);
		}
		
		for (Store s : stores) {
			dao.delete(s);
		}
	}

}
