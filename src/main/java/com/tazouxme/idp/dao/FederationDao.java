package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IFederationDao;
import com.tazouxme.idp.dao.query.FederationQueries;
import com.tazouxme.idp.exception.FederationException;
import com.tazouxme.idp.model.Federation;

public class FederationDao implements IFederationDao {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Federation> findByURN(String urn, String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_URN, Federation.class).
			setParameter(FederationQueries.PARAM_URN, urn).
			setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Set<Federation> findByOrganization(String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_ORGANIZATION, Federation.class).
			setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_USER, Federation.class).
			setParameter(FederationQueries.PARAM_USER_ID, userExternalId).
			setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId) throws FederationException {
		try {
			return em.createNamedQuery(FederationQueries.NQ_FIND_BY_USER_AND_URN, Federation.class).
				setParameter(FederationQueries.PARAM_URN, urn).
				setParameter(FederationQueries.PARAM_USER_ID, userExternalId).
				setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId).
				getSingleResult();
		} catch (Exception e) {
			throw new FederationException("Cannot retrieve Federation for User", e);
		}
	}

	@Override
	public Federation create(Federation federation) throws FederationException {
		try {
			em.persist(federation);
			return federation;
		} catch (Exception e) {
			throw new FederationException("Cannot create Federation", e);
		}
	}

	@Override
	public Federation update(Federation federation) throws FederationException {
		try {
			return em.merge(federation);
		} catch (Exception e) {
			throw new FederationException("Cannot update Federation", e);
		}
	}

	@Override
	public void delete(Federation federation) throws FederationException {
		try {
			em.remove(federation);
		} catch (Exception e) {
			throw new FederationException("Cannot delete Federation", e);
		}
	}

}
