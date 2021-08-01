package com.tazouxme.idp.dao.query;

public class OrganizationQueries {
	
	public static final String PARAM_DOMAIN = "domain";
	public static final String PARAM_ID = "externalId";
	
	public static final String FIND_BY_DOMAIN = "select o from Organization o where o.domain = :" + PARAM_DOMAIN;
	public static final String FIND_BY_EXTERNAL_ID = "select o from Organization o where o.externalId = :" + PARAM_ID;

}
