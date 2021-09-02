package com.tazouxme.idp.bo.contract;

import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.model.User;

public interface IUserBo {
	
	@Transactional(readOnly = true)
	public User findByExternalId(String externalId, String externalOrganizationId);
	
	@Transactional(readOnly = true)
	public User findByEmail(String email, String externalOrganizationId);
	
	@Transactional
	public User create(User user);
	
	@Transactional
	public User update(User user);
	
	@Transactional
	public void delete(User user);

}
