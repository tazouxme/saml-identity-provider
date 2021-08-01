package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.User;

public interface IUserDao {
	
	public User findByExternalId(String externalId, String externalOrganizationId) throws UserException;
	
	public User findByEmail(String email, String externalOrganizationId) throws UserException;
	
	public User create(User user) throws UserException;
	
	public User update(User user) throws UserException;
	
	public void delete(User user) throws UserException;

}
