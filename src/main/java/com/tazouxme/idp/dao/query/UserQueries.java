package com.tazouxme.idp.dao.query;

public class UserQueries {
	
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_ORGANIZATION_ID = "orgExternalId";
	
	public static final String FIND_BY_EMAIL = "select distinct u from User u "
			+ "left join fetch u.details d left join fetch d.claim left join fetch u.federations join fetch u.organization o "
			+ "left join fetch u.accesses a left join fetch a.application "
			+ "where u.email = :" + PARAM_EMAIL + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_EXTERNAL_ID = "select distinct u from User u "
			+ "left join fetch u.details d left join fetch d.claim left join fetch u.federations join fetch u.organization o "
			+ "left join fetch u.accesses a left join fetch a.application "
			+ "where u.externalId = :" + PARAM_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_BY_EMAIL = "User.findByEmail";
	public static final String NQ_FIND_BY_EXTERNAL_ID = "User.findByExternalId";

}
