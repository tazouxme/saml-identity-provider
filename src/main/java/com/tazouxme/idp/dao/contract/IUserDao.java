package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.model.User;

public interface IUserDao {
	
	public User findByExternalId(String externalId, String externalOrganizationId);
	
	public User findByEmail(String email, String externalOrganizationId);
	
	public User create(User user);
	
	public User update(User user);
	
	public void delete(User user);

}
