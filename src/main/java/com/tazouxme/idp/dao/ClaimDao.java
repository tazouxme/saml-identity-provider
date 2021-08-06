package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.IClaimDao;
import com.tazouxme.idp.dao.query.ClaimQueries;
import com.tazouxme.idp.exception.ClaimException;
import com.tazouxme.idp.model.Claim;

public class ClaimDao implements IClaimDao {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Claim> findAll(String externalOrganizationId) {
		return em.createNamedQuery(ClaimQueries.NQ_FIND_ALL, Claim.class).
			setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Claim findByExternalId(String externalId, String externalOrganizationId) throws ClaimException {
		try {
			return em.createNamedQuery(ClaimQueries.NQ_FIND_BY_ID, Claim.class).
				setParameter(ClaimQueries.PARAM_ID, externalId).
				setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				getSingleResult();
		} catch (Exception e) {
			throw new ClaimException("Cannot retrieve Claim for given ID", e);
		}
	}
	
	@Override
	public Claim findByURI(String uri, String externalOrganizationId) throws ClaimException {
		try {
			return em.createNamedQuery(ClaimQueries.NQ_FIND_BY_URI, Claim.class).
				setParameter(ClaimQueries.PARAM_URI, uri).
				setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				getSingleResult();
		} catch (Exception e) {
			throw new ClaimException("Cannot retrieve Claim for given URI", e);
		}
	}

	@Override
	public Claim create(Claim claim) throws ClaimException {
		try {
			em.persist(claim);
			return claim;
		} catch (Exception e) {
			throw new ClaimException("Cannot create Claim", e);
		}
	}

	@Override
	public Claim update(Claim claim) throws ClaimException {
		try {
			return em.merge(claim);
		} catch (Exception e) {
			throw new ClaimException("Cannot update Claim", e);
		}
	}

	@Override
	public void delete(Claim claim) throws ClaimException {
		try {
			em.remove(claim);
		} catch (Exception e) {
			throw new ClaimException("Cannot delete Claim", e);
		}
	}

}
