package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IRoleDao;
import com.tazouxme.idp.dao.query.RoleQueries;
import com.tazouxme.idp.exception.RoleException;
import com.tazouxme.idp.model.Role;

public class RoleDao implements IRoleDao {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Set<Role> findAll(String externalOrganizationId) {
		return em.createNamedQuery(RoleQueries.NQ_FIND_ALL, Role.class).
			setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Role findByExternalId(String externalId, String externalOrganizationId) throws RoleException {
		try {
			return em.createNamedQuery(RoleQueries.NQ_FIND_BY_ID, Role.class).
				setParameter(RoleQueries.PARAM_ID, externalId).
				setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				getSingleResult();
		} catch (Exception e) {
			throw new RoleException("Cannot retrieve Role for given ID", e);
		}
	}

	@Override
	public Role findByURI(String uri, String externalOrganizationId) throws RoleException {
		try {
			return em.createNamedQuery(RoleQueries.NQ_FIND_BY_URI, Role.class).
				setParameter(RoleQueries.PARAM_URI, uri).
				setParameter(RoleQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				getSingleResult();
		} catch (Exception e) {
			throw new RoleException("Cannot retrieve Role for given URI", e);
		}
	}

	@Override
	public Role create(Role role) throws RoleException {
		try {
			em.persist(role);
			return role;
		} catch (Exception e) {
			throw new RoleException("Cannot create Role", e);
		}
	}

	@Override
	public Role update(Role role) throws RoleException {
		try {
			return em.merge(role);
		} catch (Exception e) {
			throw new RoleException("Cannot update Role", e);
		}
	}

	@Override
	public void delete(Role role) throws RoleException {
		try {
			em.remove(role);
		} catch (Exception e) {
			throw new RoleException("Cannot delete Role", e);
		}
	}

}
