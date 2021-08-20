package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IStoreDao;
import com.tazouxme.idp.dao.query.StoreQueries;
import com.tazouxme.idp.exception.StoreException;
import com.tazouxme.idp.model.Store;

public class StoreDao implements IStoreDao {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Store> findAll(String context, String organizationId) {
		return em.createNamedQuery(StoreQueries.NQ_FIND_ALL, Store.class).
				setParameter(StoreQueries.PARAM_CONTEXT, context).
				setParameter(StoreQueries.PARAM_ORGANIZATION_ID, organizationId).
				getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Store findByKey(String context, String key, String organizationId) throws StoreException {
		try {
			return em.createNamedQuery(StoreQueries.NQ_FIND_BY_KEY, Store.class).
					setParameter(StoreQueries.PARAM_CONTEXT, context).
					setParameter(StoreQueries.PARAM_KEY, key).
					setParameter(StoreQueries.PARAM_ORGANIZATION_ID, organizationId).
					getSingleResult();
		} catch (Exception e) {
			throw new StoreException("Cannot retrieve Store", e);
		}
	}

	@Override
	public Store findByKeyAndVersion(String context, String key, long version, String organizationId) throws StoreException {
		try {
			return em.createNamedQuery(StoreQueries.NQ_FIND_BY_KEY_AND_VERSION, Store.class).
					setParameter(StoreQueries.PARAM_CONTEXT, context).
					setParameter(StoreQueries.PARAM_KEY, key).
					setParameter(StoreQueries.PARAM_VERSION, version).
					setParameter(StoreQueries.PARAM_ORGANIZATION_ID, organizationId).
					getSingleResult();
		} catch (Exception e) {
			throw new StoreException("Cannot retrieve Store", e);
		}
	}

	@Override
	public Store create(Store store) throws StoreException {
		try {
			em.persist(store);
			return store;
		} catch (Exception e) {
			throw new StoreException("Cannot create Store", e);
		}
	}

	@Override
	public Store update(Store store) throws StoreException {
		try {
			return em.merge(store);
		} catch (Exception e) {
			throw new StoreException("Cannot update Store", e);
		}
	}

	@Override
	public void delete(Store store) throws StoreException {
		try {
			em.remove(store);
		} catch (Exception e) {
			throw new StoreException("Cannot delete Store", e);
		}
	}

}
