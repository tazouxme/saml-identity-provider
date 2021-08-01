package com.tazouxme.idp.dao.query;

public class UserQueries {
	
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_ORGANIZATION_ID = "orgExternalId";
	
	public static final String FIND_BY_EMAIL = "select u from User u join fetch u.organization o where u.email = :" + PARAM_EMAIL + 
			" and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_EXTERNAL_ID = "select u from User u join fetch u.organization o where u.externalId = :" + PARAM_ID +
			" and o.externalId = :" + PARAM_ORGANIZATION_ID;

}
