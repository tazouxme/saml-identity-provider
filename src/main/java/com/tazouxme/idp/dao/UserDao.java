package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.IUserDao;
import com.tazouxme.idp.dao.query.UserQueries;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.User;

public class UserDao implements IUserDao {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public User findByExternalId(String externalId, String externalOrganizationId) throws UserException {
		try {
			return em.createNamedQuery(UserQueries.NQ_FIND_BY_EXTERNAL_ID, User.class).
				setParameter(UserQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				setParameter(UserQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new UserException("Cannot retrieve User with given ID", e);
		}
	}

	@Override
	public User findByEmail(String email, String externalOrganizationId) throws UserException {
		try {
			return em.createNamedQuery(UserQueries.NQ_FIND_BY_EMAIL, User.class).
				setParameter(UserQueries.PARAM_ORGANIZATION_ID, externalOrganizationId).
				setParameter(UserQueries.PARAM_EMAIL, email).
				getSingleResult();
		} catch (Exception e) {
			throw new UserException("Cannot retrieve User with given email", e);
		}
	}

	@Override
	public User create(User user) throws UserException {
		try {
			em.persist(user);
			return user;
		} catch (Exception e) {
			throw new UserException("Cannot create User", e);
		}
	}

	@Override
	public User update(User user) throws UserException {
		try {
			return em.merge(user);
		} catch (Exception e) {
			throw new UserException("Cannot update User", e);
		}
	}

	@Override
	public void delete(User user) throws UserException {
		try {
			em.remove(user);
		} catch (Exception e) {
			throw new UserException("Cannot delete User", e);
		}
	}

}
