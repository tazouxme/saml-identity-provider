package com.tazouxme.idp.dao.query;

public class ActivationQueries {
	
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_ORG_ID = "orgExternalId";
	public static final String PARAM_USER_ID = "userExternalId";
	public static final String PARAM_STEP = "step";
	
	public static final String FIND_BY_ID = "select s from Activation s where s.externalId = :" + PARAM_ID;
	public static final String FIND = "select s from Activation s where s.externalId = :" + PARAM_ID +
			" and s.userExternalId = :" + PARAM_USER_ID + " and s.organizationExternalId = :" + PARAM_ORG_ID + " and s.step = :" + PARAM_STEP;

}
