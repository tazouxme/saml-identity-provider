package com.tazouxme.idp.dao;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.dao.contract.IActivationDao;
import com.tazouxme.idp.dao.query.ActivationQueries;
import com.tazouxme.idp.exception.ActivationException;
import com.tazouxme.idp.model.Activation;

public class ActivationDao implements IActivationDao {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public Activation find(String orgExternalId, String userExternalId, String step) throws ActivationException {
		try {
			return em.createNamedQuery(ActivationQueries.NQ_FIND, Activation.class).
				setParameter(ActivationQueries.PARAM_ORG_ID, orgExternalId).
				setParameter(ActivationQueries.PARAM_USER_ID, userExternalId).
				setParameter(ActivationQueries.PARAM_STEP, step).
				getSingleResult();
		} catch (Exception e) {
			throw new ActivationException("Cannot retrieve Activation for given ID", e);
		}
	}
	
	@Override
	public Set<Activation> findByUser(String userExternalId, String organizationId) {
		return em.createNamedQuery(ActivationQueries.NQ_FIND_BY_USER, Activation.class).
			setParameter(ActivationQueries.PARAM_USER_ID, userExternalId).
			setParameter(ActivationQueries.PARAM_ORG_ID, organizationId).
			getResultStream().collect(Collectors.toSet());
	}

	@Override
	public Activation findByExternalId(String externalId) throws ActivationException {
		try {
			return em.createNamedQuery(ActivationQueries.NQ_FIND_BY_ID, Activation.class).
				setParameter(ActivationQueries.PARAM_ID, externalId).
				getSingleResult();
		} catch (Exception e) {
			throw new ActivationException("Cannot retrieve Activation for given ID", e);
		}
	}

	@Override
	public Activation create(Activation activation) throws ActivationException {
		try {
			em.persist(activation);
			return activation;
		} catch (Exception e) {
			throw new ActivationException("Cannot create Activation", e);
		}
	}

	@Override
	public void delete(Activation activation) throws ActivationException {
		try {
			em.remove(activation);
		} catch (Exception e) {
			throw new ActivationException("Cannot delete activation", e);
		}
	}

}
