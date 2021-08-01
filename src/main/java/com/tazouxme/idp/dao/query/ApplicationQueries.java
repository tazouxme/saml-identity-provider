package com.tazouxme.idp.dao.query;

public class ApplicationQueries {
	
	public static final String PARAM_URN = "urn";
	public static final String PARAM_ID = "externalId";
	
	public static final String FIND_BY_URN = "select s from Application s where s.urn = :" + PARAM_URN;
	public static final String FIND_BY_EXTERNAL_ID = "select s from Application s where s.externalId = :" + PARAM_ID;

}
