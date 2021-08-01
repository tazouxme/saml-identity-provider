package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.IAccessDao;
import com.tazouxme.idp.dao.query.AccessQueries;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.model.Access;

public class AccessDao implements IAccessDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Access findByExternalId(String externalId) throws AccessException {
		try {
			return em.createNamedQuery("Access.findByExternalId", Access.class).
				setParameter(AccessQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new AccessException("Cannot retrieve Access for given ID", e);
		}
	}

	@Override
	public Access findByOrganization(String organizationExternalId, String urn) throws AccessException {
		try {
			return em.createNamedQuery("Access.findByOrganization", Access.class).
				setParameter(AccessQueries.PARAM_URN, urn).
				setParameter(AccessQueries.PARAM_KEY, organizationExternalId).
				getSingleResult();
		} catch (Exception e) {
			throw new AccessException("Cannot retrieve Access for Organization", e);
		}
	}

	@Override
	public Access findByUser(String userExternalId, String urn) throws AccessException {
		try {
			return em.createNamedQuery("Access.findByUser", Access.class).
				setParameter(AccessQueries.PARAM_URN, urn).
				setParameter(AccessQueries.PARAM_KEY, userExternalId).
				getSingleResult();
		} catch (Exception e) {
			throw new AccessException("Cannot retrieve Access for User", e);
		}
	}

	@Override
	public Access create(Access access) throws AccessException {
		try {
			em.persist(access);
			return access;
		} catch (Exception e) {
			throw new AccessException("Cannot create Access", e);
		}
	}

	@Override
	public Access update(Access access) throws AccessException {
		try {
			return em.merge(access);
		} catch (Exception e) {
			throw new AccessException("Cannot update Access", e);
		}
	}

	@Override
	public void delete(Access access) throws AccessException {
		try {
			em.remove(access);
		} catch (Exception e) {
			throw new AccessException("Cannot delete Access", e);
		}
	}

}
