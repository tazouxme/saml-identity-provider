package com.tazouxme.idp.security.filter.sso.http;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.stage.chain.StageChain;

public class GetSingleSignOn extends AbstractHttpSingleSignOn {

	public GetSingleSignOn(IdentityProviderConfiguration configuration) {
		super(configuration);
	}

	public GetSingleSignOn(IdentityProviderConfiguration configuration, StageChain stages) {
		super(configuration, stages);
	}

	@Override
	public String getMethod() {
		return "GET";
	}
	
}
