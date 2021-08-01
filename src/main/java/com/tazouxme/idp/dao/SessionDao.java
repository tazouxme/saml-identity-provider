package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tazouxme.idp.dao.contract.ISessionDao;
import com.tazouxme.idp.dao.query.SessionQueries;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.model.Session;

public class SessionDao implements ISessionDao {
	
	@PersistenceContext
	private EntityManager em;

	@Override
	public Session find(String orgExternalId, String userExternalId) throws SessionException {
		try {
			return em.createNamedQuery("Session.find", Session.class).
				setParameter(SessionQueries.PARAM_ORGANIZATION_ID, orgExternalId).
				setParameter(SessionQueries.PARAM_USER_ID, userExternalId).
				getSingleResult();
		} catch (Exception e) {
			throw new SessionException("Cannot retrieve Session", e);
		}
	}

	@Override
	public Session create(Session session) throws SessionException {
		try {
			em.persist(session);
			return session;
		} catch (Exception e) {
			throw new SessionException("Cannot create Session", e);
		}
	}

	@Override
	public void delete(Session session) throws SessionException {
		try {
			em.remove(session);
		} catch (Exception e) {
			throw new SessionException("Cannot delete Session", e);
		}
	}

}
