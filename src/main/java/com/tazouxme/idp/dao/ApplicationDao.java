package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IApplicationDao;
import com.tazouxme.idp.dao.query.ApplicationQueries;
import com.tazouxme.idp.model.Application;

public class ApplicationDao implements IApplicationDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Application> findAll(String organizationExternalId) {
		return em.createNamedQuery(ApplicationQueries.NQ_FIND_ALL, Application.class)
				.setParameter(ApplicationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Application findByUrn(String urn, String organizationExternalId) {
		return em.createNamedQuery(ApplicationQueries.NQ_FIND_BY_URN, Application.class)
				.setParameter(ApplicationQueries.PARAM_URN, urn)
				.setParameter(ApplicationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getSingleResult();
	}

	@Override
	public Application findByExternalId(String externalId, String organizationExternalId) {
		return em.createNamedQuery(ApplicationQueries.NQ_FIND_BY_EXTERNAL_ID, Application.class)
				.setParameter(ApplicationQueries.PARAM_ID, externalId)
				.setParameter(ApplicationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getSingleResult();
	}

	@Override
	public Application create(Application application) {
		em.persist(application);
		return application;
	}

	@Override
	public Application update(Application application) {
		return em.merge(application);
	}

	@Override
	public void delete(Application application) {
		em.remove(application);
	}

}
