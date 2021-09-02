package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IAccessDao;
import com.tazouxme.idp.dao.query.AccessQueries;
import com.tazouxme.idp.model.Access;

public class AccessDao implements IAccessDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Access findByExternalId(String externalId, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_ID, Access.class)
				.setParameter(AccessQueries.PARAM_ID, externalId)
				.setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getSingleResult();
	}

	@Override
	public Set<Access> findByURN(String urn, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_URN, Access.class)
				.setParameter(AccessQueries.PARAM_URN, urn)
				.setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Access> findByOrganization(String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_ORGANIZATION, Access.class)
				.setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Access> findByUser(String userExternalId, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_USER, Access.class)
				.setParameter(AccessQueries.PARAM_USER_ID, userExternalId)
				.setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Access findByUserAndURN(String userExternalId, String urn, String organizationExternalId) {
		return em.createNamedQuery(AccessQueries.NQ_FIND_BY_USER_AND_URN, Access.class)
				.setParameter(AccessQueries.PARAM_URN, urn)
				.setParameter(AccessQueries.PARAM_USER_ID, userExternalId)
				.setParameter(AccessQueries.PARAM_ORGANIZATION_ID, organizationExternalId)
				.getSingleResult();
	}

	@Override
	public Access create(Access access) {
		em.persist(access);
		return access;
	}

	@Override
	public Access update(Access access) {
		return em.merge(access);
	}

	@Override
	public void delete(Access access) {
		em.remove(access);
	}

}
