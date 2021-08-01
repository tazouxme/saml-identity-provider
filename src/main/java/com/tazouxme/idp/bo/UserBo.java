package com.tazouxme.idp.bo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.dao.contract.IUserDao;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.util.IDUtils;

public class UserBo implements IUserBo {
	
	@Autowired
	private IUserDao dao;

	@Override
	public User findByExternalId(String externalId, String externalOrganizationId) throws UserException {
		return dao.findByExternalId(externalId, externalOrganizationId);
	}

	@Override
	public User findByEmail(String email, String externalOrganizationId) throws UserException {
		return dao.findByEmail(email, externalOrganizationId);
	}

	@Override
	public User create(User user) throws UserException {
		user.setExternalId(IDUtils.generateId("USE_", 8));
		user.setCreationDate(new Date().getTime());
		return dao.create(user);
	}

	@Override
	public User update(User user) throws UserException {
		User pUser = findByExternalId(user.getExternalId(), user.getOrganization().getExternalId());
		pUser.setEnabled(user.isEnabled());
		pUser.setPassword(user.getPassword());
		pUser.setFirstname(user.getFirstname());
		pUser.setLastname(user.getLastname());
		pUser.setSex(user.getSex());
		pUser.setBirthDate(user.getBirthDate());
		pUser.setPicture(user.getPicture());
		pUser.setCity(user.getCity());
		pUser.setCountry(user.getCountry());
		pUser.setStreet(user.getStreet());
		pUser.setZipCode(user.getZipCode());
		
		return dao.update(user);
	}

	@Override
	public void delete(User user) throws UserException {
		dao.delete(findByExternalId(user.getExternalId(), user.getOrganization().getExternalId()));
	}

}
