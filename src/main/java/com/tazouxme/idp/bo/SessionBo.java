package com.tazouxme.idp.bo;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.dao.contract.ISessionDao;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.util.IDUtils;

public class SessionBo implements ISessionBo {
	
	@Autowired
	private ISessionDao dao;

	@Override
	public Session find(String orgExternalId, String userExternalId) {
		return dao.find(orgExternalId, userExternalId);
	}

	@Override
	public Session create(Session session) {
		session.setToken(UUID.randomUUID().toString());
		session.setExternalId(IDUtils.generateId("SES_", 8));
		session.setCreationDate(new Date().getTime());
		session.setStatus(1);
		
		return dao.create(session);
	}

	@Override
	public void delete(Session session) {
		dao.delete(find(session.getOrganizationExternalId(), session.getUserExternalId()));
	}

}
