package com.tazouxme.idp.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.ISessionDao;
import com.tazouxme.idp.dao.query.SessionQueries;
import com.tazouxme.idp.model.Session;

public class SessionDao implements ISessionDao {

	protected final Log logger = LogFactory.getLog(getClass());

	@PersistenceContext
	private EntityManager em;

	@Override
	public Session find(String orgExternalId, String userExternalId) {
		return em.createNamedQuery(SessionQueries.NQ_FIND, Session.class)
				.setParameter(SessionQueries.PARAM_ORGANIZATION_ID, orgExternalId)
				.setParameter(SessionQueries.PARAM_USER_ID, userExternalId)
				.getSingleResult();
	}

	@Override
	public Session create(Session session) {
		em.persist(session);
		return session;
	}

	@Override
	public void delete(Session session) {
		em.remove(session);
	}

}
