package com.tazouxme.idp.dao.query;

public class StoreQueries {
	
	public static final String PARAM_CONTEXT = "context";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_VERSION = "version";
	public static final String PARAM_ORGANIZATION_ID = "organizationId";
	
	public static final String FIND_ALL = "select s from Store s join fetch s.organization o "
			+ "where s.context = :" + PARAM_CONTEXT + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_KEY = "select s from Store s join fetch s.organization o "
			+ "where s.context = :" + PARAM_CONTEXT + " and s.storeKey = :" + PARAM_KEY + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	public static final String FIND_BY_KEY_AND_VERSION = "select s from Store s join fetch s.organization o "
			+ "where s.context = :" + PARAM_CONTEXT + " and s.storeKey = :" + PARAM_KEY + " and s.version = :" + PARAM_VERSION + " and o.externalId = :" + PARAM_ORGANIZATION_ID;
	
	public static final String NQ_FIND_ALL = "Store.findAll";
	public static final String NQ_FIND_BY_KEY = "Store.findByKey";
	public static final String NQ_FIND_BY_KEY_AND_VERSION = "Store.findByKeyAndVersion";

}
