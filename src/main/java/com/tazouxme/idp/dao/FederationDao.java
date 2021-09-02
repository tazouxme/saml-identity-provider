package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IFederationDao;
import com.tazouxme.idp.dao.query.FederationQueries;
import com.tazouxme.idp.model.Federation;

public class FederationDao implements IFederationDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Federation> findByURN(String urn, String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_URN, Federation.class)
				.setParameter(FederationQueries.PARAM_URN, urn)
				.setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Federation> findByOrganization(String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_ORGANIZATION, Federation.class)
				.setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Federation> findByUser(String userExternalId, String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_USER, Federation.class)
				.setParameter(FederationQueries.PARAM_USER_ID, userExternalId)
				.setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Federation findByUserAndURN(String userExternalId, String urn, String organizationExternalId) {
		return em.createNamedQuery(FederationQueries.NQ_FIND_BY_USER_AND_URN, Federation.class)
				.setParameter(FederationQueries.PARAM_URN, urn)
				.setParameter(FederationQueries.PARAM_USER_ID, userExternalId)
				.setParameter(FederationQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getSingleResult();
	}

	@Override
	public Federation create(Federation federation) {
		em.persist(federation);
		return federation;
	}

	@Override
	public Federation update(Federation federation) {
		return em.merge(federation);
	}

	@Override
	public void delete(Federation federation) {
		em.remove(federation);
	}

}
