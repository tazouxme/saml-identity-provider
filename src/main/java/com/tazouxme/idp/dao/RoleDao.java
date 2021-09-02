package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IRoleDao;
import com.tazouxme.idp.dao.query.RoleQueries;
import com.tazouxme.idp.model.Role;

public class RoleDao implements IRoleDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Role> findAll(String externalOrganizationId) {
		return em.createNamedQuery(RoleQueries.NQ_FIND_ALL, Role.class)
				.setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getResultStream()
				.collect(Collectors.toSet());
	}

	@Override
	public Role findByExternalId(String externalId, String externalOrganizationId) {
		return em.createNamedQuery(RoleQueries.NQ_FIND_BY_ID, Role.class)
				.setParameter(RoleQueries.PARAM_ID, externalId)
				.setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getSingleResult();
	}

	@Override
	public Role findByURI(String uri, String externalOrganizationId) {
		return em.createNamedQuery(RoleQueries.NQ_FIND_BY_URI, Role.class)
				.setParameter(RoleQueries.PARAM_URI, uri)
				.setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.getSingleResult();
	}

	@Override
	public Role create(Role role) {
		em.persist(role);
		return role;
	}

	@Override
	public Role update(Role role) {
		return em.merge(role);
	}

	@Override
	public void delete(Role role) {
		em.remove(role);
	}

}
