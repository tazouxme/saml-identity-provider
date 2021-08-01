package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.User;

public interface IUserBo {
	
	@Transactional(readOnly = true)
	public User findByExternalId(String externalId, String externalOrganizationId) throws UserException;
	
	@Transactional(readOnly = true)
	public User findByEmail(String email, String externalOrganizationId) throws UserException;
	
	@Transactional
	public User create(User user) throws UserException;
	
	@Transactional
	public User update(User user) throws UserException;
	
	@Transactional
	public void delete(User user) throws UserException;

}
