package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.IApplicationDao;
import com.tazouxme.idp.dao.query.ApplicationQueries;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.model.Application;

public class ApplicationDao implements IApplicationDao {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Application findByUrn(String urn) throws ApplicationException {
		try {
			return em.createNamedQuery("Application.findByUrn", Application.class).
				setParameter(ApplicationQueries.PARAM_URN, urn).
				getSingleResult();
		} catch (Exception e) {
			throw new ApplicationException("Cannot retrieve Application with given URN", e);
		}
	}

	@Override
	public Application findByExternalId(String externalId) throws ApplicationException {
		try {
			return em.createNamedQuery("Application.findByExternalId", Application.class).
				setParameter(ApplicationQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new ApplicationException("Cannot retrieve Application with given ID", e);
		}
	}

	@Override
	public Application create(Application application) throws ApplicationException {
		try {
			em.persist(application);
			return application;
		} catch (Exception e) {
			throw new ApplicationException("Cannot create Application", e);
		}
	}

	@Override
	public Application update(Application application) throws ApplicationException {
		try {
			return em.merge(application);
		} catch (Exception e) {
			throw new ApplicationException("Cannot update Application", e);
		}
	}

	@Override
	public void delete(Application application) throws ApplicationException {
		try {
			em.remove(application);
		} catch (Exception e) {
			throw new ApplicationException("Cannot delete Application", e);
		}
	}

}
