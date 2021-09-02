package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IOrganizationDao;
import com.tazouxme.idp.dao.query.OrganizationQueries;
import com.tazouxme.idp.model.Organization;

public class OrganizationDao implements IOrganizationDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Organization findByExternalId(String externalId) {
		return em.createNamedQuery(OrganizationQueries.NQ_FIND_BY_EXTERNAL_ID, Organization.class)
				.setParameter(OrganizationQueries.PARAM_ID, externalId).
				getSingleResult();
	}

	@Override
	public Organization findByDomain(String domain) {
		return em.createNamedQuery(OrganizationQueries.NQ_FIND_BY_DOMAIN, Organization.class)
				.setParameter(OrganizationQueries.PARAM_DOMAIN, domain).
				getSingleResult();
	}

	@Override
	public Organization create(Organization org) {
		em.persist(org);
		return org;
	}

	@Override
	public Organization update(Organization org) {
		return em.merge(org);
	}

	@Override
	public void delete(Organization org) {
		em.remove(org);
	}

}
