package com.tazouxme.idp.bo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
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
		pUser.setAdministrator(user.isAdministrator());
		pUser.setEnabled(user.isEnabled());
		
		if (!StringUtils.isBlank(user.getPassword())) {
			pUser.setPassword(user.getPassword());
		}
		
		return dao.update(pUser);
	}

	@Override
	public void delete(User user) throws UserException {
		dao.delete(findByExternalId(user.getExternalId(), user.getOrganization().getExternalId()));
	}

}
