package com.tazouxme.idp.test.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

public class DbKiller {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void killAll() {
		int count = 0;
		
		logger.info("Killing Activation...");
		count = em.createQuery("delete from Activation a").executeUpdate();
		logger.info(count + " Killed Activation");
		
		logger.info("Killing Session...");
		count = em.createQuery("delete from Session a").executeUpdate();
		logger.info(count + " Killed Session");
		
		logger.info("Killing UserDetails...");
		count = em.createQuery("delete from UserDetails a").executeUpdate();
		logger.info(count + " Killed UserDetails");
		
		logger.info("Killing Federation...");
		count = em.createQuery("delete from Federation a").executeUpdate();
		logger.info(count + " Killed Federation");
		
		logger.info("Killing Access...");
		count = em.createQuery("delete from Access a").executeUpdate();
		logger.info(count + " Killed Access");
		
		logger.info("Killing Claim...");
		count = em.createQuery("delete from Claim a").executeUpdate();
		logger.info(count + " Killed Claim");
		
		logger.info("Killing Role...");
		count = em.createQuery("delete from Role a").executeUpdate();
		logger.info(count + " Killed Role");
		
		logger.info("Killing Application...");
		count = em.createQuery("delete from Application a").executeUpdate();
		logger.info(count + " Killed Application");
		
		logger.info("Killing User...");
		count = em.createQuery("delete from User a").executeUpdate();
		logger.info(count + " Killed User");
		
		logger.info("Killing Organization...");
		count = em.createQuery("delete from Organization a").executeUpdate();
		logger.info(count + " Killed Organization");
		
	}

}
