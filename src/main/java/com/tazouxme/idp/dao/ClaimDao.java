package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IClaimDao;
import com.tazouxme.idp.dao.query.ClaimQueries;
import com.tazouxme.idp.model.Claim;

public class ClaimDao implements IClaimDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Claim> findAll(String externalOrganizationId) {
		return em.createNamedQuery(ClaimQueries.NQ_FIND_ALL, Claim.class)
				.setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Claim findByExternalId(String externalId, String externalOrganizationId) {
		return em.createNamedQuery(ClaimQueries.NQ_FIND_BY_ID, Claim.class)
				.setParameter(ClaimQueries.PARAM_ID, externalId)
				.setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getSingleResult();
	}

	@Override
	public Claim findByURI(String uri, String externalOrganizationId) {
		return em.createNamedQuery(ClaimQueries.NQ_FIND_BY_URI, Claim.class)
				.setParameter(ClaimQueries.PARAM_URI, uri)
				.setParameter(ClaimQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getSingleResult();
	}

	@Override
	public Claim create(Claim claim) {
		em.persist(claim);
		return claim;
	}

	@Override
	public Claim update(Claim claim) {
		return em.merge(claim);
	}

	@Override
	public void delete(Claim claim) {
		em.remove(claim);
	}

}
