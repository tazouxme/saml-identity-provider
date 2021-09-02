package com.tazouxme.idp.bo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.dao.contract.IUserDao;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.model.UserDetails;
import com.tazouxme.idp.util.IDUtils;

public class UserBo implements IUserBo {
	
	@Autowired
	private IUserDao dao;

	@Override
	public User findByExternalId(String externalId, String externalOrganizationId) {
		return dao.findByExternalId(externalId, externalOrganizationId);
	}

	@Override
	public User findByEmail(String email, String externalOrganizationId) {
		return dao.findByEmail(email, externalOrganizationId);
	}

	@Override
	public User create(User user) {
		user.setExternalId(IDUtils.generateId("USE_", 8));
		user.setCreationDate(new Date().getTime());
		user.setStatus(1);
		
		if (StringUtils.isBlank(user.getCreatedBy())) {
			user.setCreatedBy(user.getExternalId());
			
			for (UserDetails details : user.getDetails()) {
				details.setCreatedBy(user.getExternalId());
			}
		}
		
		return dao.create(user);
	}

	@Override
	public User update(User user) {
		User pUser = findByExternalId(user.getExternalId(), user.getOrganization().getExternalId());
		pUser.setAdministrator(user.isAdministrator());
		pUser.setEnabled(user.isEnabled());
		
		if (!StringUtils.isBlank(user.getPassword())) {
			pUser.setPassword(user.getPassword());
		}
		
		return dao.update(pUser);
	}

	@Override
	public void delete(User user) {
		dao.delete(findByExternalId(user.getExternalId(), user.getOrganization().getExternalId()));
	}

}
