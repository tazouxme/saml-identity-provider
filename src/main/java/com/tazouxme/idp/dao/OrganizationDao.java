package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.IOrganizationDao;
import com.tazouxme.idp.dao.query.OrganizationQueries;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.model.Organization;

public class OrganizationDao implements IOrganizationDao {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Organization findByExternalId(String externalId) throws OrganizationException {
		try {
			return em.createNamedQuery("Organization.findByExternalId", Organization.class).
				setParameter(OrganizationQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new OrganizationException("Cannot retrieve Organization with given ID", e);
		}
	}

	@Override
	public Organization findByDomain(String domain) throws OrganizationException {
		try {
			return em.createNamedQuery("Organization.findByDomain", Organization.class).
				setParameter(OrganizationQueries.PARAM_DOMAIN, domain).
				getSingleResult();
		} catch (Exception e) {
			throw new OrganizationException("Cannot retrieve Organization with given domain", e);
		}
	}

	@Override
	public Organization create(Organization org) throws OrganizationException {
		try {
			em.persist(org);
			return org;
		} catch (Exception e) {
			throw new OrganizationException("Cannot create Organization", e);
		}
	}

	@Override
	public Organization update(Organization org) throws OrganizationException {
		try {
			return em.merge(org);
		} catch (Exception e) {
			throw new OrganizationException("Cannot update Organization", e);
		}
	}

	@Override
	public void delete(Organization org) throws OrganizationException {
		try {
			em.remove(org);
		} catch (Exception e) {
			throw new OrganizationException("Cannot delete Organization", e);
		}
	}

}
