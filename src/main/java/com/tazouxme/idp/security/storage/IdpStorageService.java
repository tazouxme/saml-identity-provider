package com.tazouxme.idp.security.storage;

import java.io.IOException;

import org.opensaml.storage.AbstractStorageService;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.VersionMismatchException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IStoreBo;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.StoreException;
import com.tazouxme.idp.model.Store;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserIdentity;

import net.shibboleth.utilities.java.support.collection.Pair;

public class IdpStorageService extends AbstractStorageService {
	
	private IOrganizationBo organizationBo;
	private IStoreBo storeBo;
	
	public IdpStorageService(ApplicationContext applicationContext) {
		this.organizationBo = applicationContext.getBean(IOrganizationBo.class);
		this.storeBo = applicationContext.getBean(IStoreBo.class);
		
		setKeySize(Integer.MAX_VALUE);
	}

	@Override
	public boolean create(String context, String key, String value, Long expiration) throws IOException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setStoreValue(value.getBytes());
		store.setExpiration(expiration);
		store.setVersion(1L);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.create(store);
			return true;
		} catch (StoreException | OrganizationException e) {
			return false;
		}
	}

	@Override
	public <T> StorageRecord<T> read(String context, String key) throws IOException {
		try {
			Store pStore = storeBo.findByKey(context, key, findUserIdentity().getOrganizationId());
			return new StorageRecord<T>(new String(pStore.getStoreValue()), pStore.getExpiration());
		} catch (StoreException e) {
			return null;
		}
	}

	@Override
	public <T> Pair<Long, StorageRecord<T>> read(String context, String key, long version) throws IOException {
		try {
			Store pStore = storeBo.findByKeyAndVersion(context, key, version, findUserIdentity().getOrganizationId());
			return new Pair<>(pStore.getVersion(), new StorageRecord<T>(new String(pStore.getStoreValue()), pStore.getExpiration()));
		} catch (StoreException e) {
			return null;
		}
	}

	@Override
	public boolean update(String context, String key, String value, Long expiration) throws IOException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setStoreValue(value.getBytes());
		store.setExpiration(expiration);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.update(store);
			return true;
		} catch (StoreException | OrganizationException e) {
			return false;
		}
	}

	@Override
	public Long updateWithVersion(long version, String context, String key, String value, Long expiration) throws IOException, VersionMismatchException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setStoreValue(value.getBytes());
		store.setExpiration(expiration);
		store.setVersion(version);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			Store pStore = storeBo.update(store);
			return pStore.getVersion();
		} catch (StoreException | OrganizationException e) {
			return null;
		}
	}

	@Override
	public boolean updateExpiration(String context, String key, Long expiration) throws IOException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setExpiration(expiration);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.update(store);
			return true;
		} catch (StoreException | OrganizationException e) {
			return false;
		}
	}

	@Override
	public void updateContextExpiration(String context, Long expiration) throws IOException {
		Store store = new Store();
		store.setContext(context);
		store.setExpiration(expiration);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.update(store);
		} catch (StoreException | OrganizationException e) {
			return;
		}
	}

	@Override
	public boolean delete(String context, String key) throws IOException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.delete(store);
			return true;
		} catch (StoreException | OrganizationException e) {
			return false;
		}
	}

	@Override
	public boolean deleteWithVersion(long version, String context, String key) throws IOException, VersionMismatchException {
		Store store = new Store();
		store.setContext(context);
		store.setStoreKey(key);
		store.setVersion(version);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.delete(store);
			return true;
		} catch (StoreException | OrganizationException e) {
			return false;
		}
	}

	@Override
	public void deleteContext(String context) throws IOException {
		Store store = new Store();
		store.setContext(context);
		
		try {
			store.setOrganization(organizationBo.findByExternalId(findUserIdentity().getOrganizationId()));
			storeBo.delete(store);
		} catch (StoreException | OrganizationException e) {
			return;
		}
	}

	@Override
	public void reap(String context) throws IOException {
		deleteContext(context);
	}
	
	private UserIdentity findUserIdentity() {
		UserAuthenticationToken auth = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		return auth.getDetails().getIdentity();
	}

}
