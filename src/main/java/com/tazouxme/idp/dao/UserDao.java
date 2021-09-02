package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IUserDao;
import com.tazouxme.idp.dao.query.UserQueries;
import com.tazouxme.idp.model.User;

public class UserDao implements IUserDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public User findByExternalId(String externalId, String externalOrganizationId) {
		return em.createNamedQuery(UserQueries.NQ_FIND_BY_EXTERNAL_ID, User.class)
				.setParameter(UserQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.setParameter(UserQueries.PARAM_ID, externalId).getSingleResult();
	}

	@Override
	public User findByEmail(String email, String externalOrganizationId) {
		return em.createNamedQuery(UserQueries.NQ_FIND_BY_EMAIL, User.class)
				.setParameter(UserQueries.PARAM_ORGANIZATION_ID, externalOrganizationId)
				.setParameter(UserQueries.PARAM_EMAIL, email).getSingleResult();
	}

	@Override
	public User create(User user) {
		em.persist(user);
		return user;
	}

	@Override
	public User update(User user) {
		return em.merge(user);
	}

	@Override
	public void delete(User user) {
		em.remove(user);
	}

}
