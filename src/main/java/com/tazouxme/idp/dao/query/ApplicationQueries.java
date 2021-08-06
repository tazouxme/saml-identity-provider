package com.tazouxme.idp.dao.query;

public class ApplicationQueries {
	
	public static final String PARAM_URN = "urn";
	public static final String PARAM_ID = "externalId";
	
	public static final String FIND_ALL = "select distinct s from Application s "
			+ "left join fetch s.claims join fetch s.organization o "
			+ "left join fetch s.accesses a left join fetch a.user left join fetch a.role left join fetch a.organization "
			+ "where o.externalId = :" + PARAM_ID;
	public static final String FIND_BY_URN = "select distinct s from Application s "
			+ "left join fetch s.claims join fetch s.organization o "
			+ "left join fetch s.accesses a left join fetch a.user left join fetch a.role left join fetch a.organization "
			+ "where s.urn = :" + PARAM_URN + " and o.externalId = :" + PARAM_ID;
	public static final String FIND_BY_EXTERNAL_ID = "select distinct s from Application s "
			+ "left join fetch s.claims join fetch s.organization "
			+ "left join fetch s.accesses a left join fetch a.user left join fetch a.role left join fetch a.organization "
			+ "where s.externalId = :" + PARAM_ID;
	
	public static final String NQ_FIND_ALL = "Application.findAll";
	public static final String NQ_FIND_BY_URN = "Application.findByUrn";
	public static final String NQ_FIND_BY_EXTERNAL_ID = "Application.findByExternalId";

}
