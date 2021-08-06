package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

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
			return em.createNamedQuery(AccessQueries.NQ_FIND_BY_ID, Access.class).
				setParameter(AccessQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new AccessException("Cannot retrieve Access for given ID", e);
		}
	}
	
	@Override
	public Set<Access> findByURN(String urn, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_URN, Access.class).
			setParameter(AccessQueries.PARAM_URN, urn).
			setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Set<Access> findByOrganization(String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_ORGANIZATION, Access.class).
			setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Set<Access> findByUser(String userExternalId, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_USER, Access.class).
			setParameter(AccessQueries.PARAM_USER_ID, userExternalId).
			setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}
	
	@Override
	public Access findByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws AccessException {
		try {
			return em.createNamedQuery(AccessQueries.NQ_FIND_BY_USER_AND_URN, Access.class).
				setParameter(AccessQueries.PARAM_URN, urn).
				setParameter(AccessQueries.PARAM_USER_ID, userExternalId).
				setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
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
