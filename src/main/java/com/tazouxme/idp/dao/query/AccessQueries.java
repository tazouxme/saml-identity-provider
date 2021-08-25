package com.tazouxme.idp.dao.query;

public class AccessQueries {
	
	public static final String PARAM_ID = "externalId";
	public static final String PARAM_URN = "urn";
	public static final String PARAM_ORGANIZATION_ID = "externalOrganizationId";
	public static final String PARAM_USER_ID = "externalUserId";
	
	public static final String FIND_BY_ID = "select distinct s from Access s "
			+ "join fetch s.organization o join fetch s.user join fetch s.application join fetch s.role "
			+ "where s.externalId = :" + PARAM_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_URN = "select distinct s from Access s "
			+ "join fetch s.organization o join fetch s.user join fetch s.application a join fetch s.role "
			+ "where a.urn = :" + PARAM_URN + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_ORGANIZATION = "select distinct s from Access s "
			+ "join fetch s.organization o join fetch s.user join fetch s.application join fetch s.role "
			+ "where o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_USER = "select distinct s from Access s "
			+ "join fetch s.organization o join fetch s.user u join fetch s.application join fetch s.role "
			+ "where u.externalId = :" + PARAM_USER_ID + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_USER_AND_URN = "select distinct s from Access s "
			+ "join fetch s.organization o join fetch s.user u join fetch s.application a join fetch s.role "
			+ "where u.externalId = :" + PARAM_USER_ID + " and a.urn = :" + PARAM_URN + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_BY_ID = "Access.findByExternalId";
	public static final String NQ_FIND_BY_URN = "Access.findByURN";
	public static final String NQ_FIND_BY_ORGANIZATION = "Access.findByOrganization";
	public static final String NQ_FIND_BY_USER = "Access.findByUser";
	public static final String NQ_FIND_BY_USER_AND_URN = "Access.findByUserAndURN";

}
