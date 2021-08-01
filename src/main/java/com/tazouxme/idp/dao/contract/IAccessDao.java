package com.tazouxme.idp.dao.contract;

import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.model.Access;

public interface IAccessDao {
	
	public Access findByExternalId(String externalId) throws AccessException;
	
	public Access findByOrganization(String organizationExternalId, String urn) throws AccessException;
	
	public Access findByUser(String userExternalId, String urn) throws AccessException;
	
	public Access create(Access access) throws AccessException;
	
	public Access update(Access access) throws AccessException;
	
	public void delete(Access access) throws AccessException;

}
