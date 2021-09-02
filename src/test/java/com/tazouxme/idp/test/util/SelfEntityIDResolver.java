package com.tazouxme.idp.test.util;

import java.util.ArrayList;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

public class SelfEntityIDResolver implements Resolver<String, CriteriaSet> {
	
	private String urn;
	
	public SelfEntityIDResolver(String urn) {
		this.urn = urn;
	}

	@Override
	public Iterable<String> resolve(CriteriaSet criteria) throws ResolverException {
		return new ArrayList<>();
	}

	@Override
	public String resolveSingle(CriteriaSet criteria) throws ResolverException {
		return this.urn;
	}

}
