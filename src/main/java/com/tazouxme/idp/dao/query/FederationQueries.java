package com.tazouxme.idp.dao.query;

public class FederationQueries {
	
	public static final String PARAM_URN = "urn";
	public static final String PARAM_ORGANIZATION_ID = "externalOrganizationId";
	public static final String PARAM_USER_ID = "externalUserId";
	
	public static final String FIND_BY_URN = "select distinct s from Federation s "
			+ "join fetch s.organization o join fetch s.user join fetch s.application a "
			+ "where a.urn = :" + PARAM_URN + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_ORGANIZATION = "select distinct s from Federation s "
			+ "join fetch s.organization o join fetch s.user join fetch s.application a "
			+ "where o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_USER = "select distinct s from Federation s "
			+ "join fetch s.organization o join fetch s.user u join fetch s.application a "
			+ "where u.externalId = :" + PARAM_USER_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_USER_AND_URN = "select distinct s from Federation s "
			+ "join fetch s.organization o join fetch s.user u join fetch s.application a "
			+ "where u.externalId = :" + PARAM_USER_ID + " and a.urn = :" + PARAM_URN + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_BY_URN = "Federation.findByURN";
	public static final String NQ_FIND_BY_ORGANIZATION = "Federation.findByOrganization";
	public static final String NQ_FIND_BY_USER = "Federation.findByUser";
	public static final String NQ_FIND_BY_USER_AND_URN = "Federation.findByUserAndURN";

}
