package com.tazouxme.idp.dao.query;

public class ClaimQueries {
	
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_URI = "uri";
	public static final String PARAM_ORGANIZATION_ID = "orgExternalId";
	
	public static final String FIND_ALL = "select s from Claim s "
			+ "join fetch s.organization o left join fetch o.users "
			+ "where o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_ID = "select s from Claim s "
			+ "join fetch s.organization o left join fetch o.users "
			+ "where s.externalId = :" + PARAM_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_URI = "select s from Claim s "
			+ "join fetch s.organization o left join fetch o.users "
			+ "where s.uri = :" + PARAM_URI + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_ALL = "Claim.findAll";
	public static final String NQ_FIND_BY_ID = "Claim.findByExternalId";
	public static final String NQ_FIND_BY_URI = "Claim.findByURI";

}
