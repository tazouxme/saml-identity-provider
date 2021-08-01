package com.tazouxme.idp.dao.query;

public class AccessQueries {
	
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_URN = "urn";
	public static final String PARAM_KEY = "key";
	
	public static final String FIND_BY_ID = "select s from Access s where s.externalId = :" + PARAM_ID;
	public static final String FIND_BY_ORGANIZATION = "select s from Access s where s.urn = :" + PARAM_URN +
			" and s.accessType = 'ORG' and s.accessKey = :" + PARAM_KEY;
	public static final String FIND_BY_USER = "select s from Access s where s.urn = :" + PARAM_URN +
			" and s.accessType = 'USER' and s.accessKey = :" + PARAM_KEY;

}
