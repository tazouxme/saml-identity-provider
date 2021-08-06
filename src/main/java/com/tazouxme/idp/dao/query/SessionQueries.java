package com.tazouxme.idp.dao.query;

public class SessionQueries {
	
	public static final String PARAM_ORGANIZATION_ID = "orgExternalId";
	public static final String PARAM_USER_ID = "userExternalId";
	
	public static final String FIND = "select s from Session s where s.organizationExternalId = :" + PARAM_ORGANIZATION_ID + 
			" and s.userExternalId = :" + PARAM_USER_ID;
	
	public static final String NQ_FIND = "Session.find";

}
