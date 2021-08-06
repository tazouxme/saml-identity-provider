package com.tazouxme.idp.dao.query;

public class RoleQueries {
	
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_URI = "uri";
	public static final String PARAM_ORGANIZATION_ID = "orgExternalId";
	
	public static final String FIND_ALL = "select s from Role s join fetch s.organization o "
			+ "where o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_ID = "select s from Role s join fetch s.organization o "
			+ "where s.externalId = :" + PARAM_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_URI = "select s from Role s join fetch s.organization o "
			+ "where s.uri = :" + PARAM_URI + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_ALL = "Role.findAll";
	public static final String NQ_FIND_BY_ID = "Role.findByExternalId";
	public static final String NQ_FIND_BY_URI = "Role.findByURI";

}
