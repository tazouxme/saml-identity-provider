package com.tazouxme.idp.security.filter.slo.http;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.stage.chain.StageChain;

public class GetSingleLogout extends AbstractHttpSingleLogout {

	public GetSingleLogout(IdentityProviderConfiguration configuration) {
		super(configuration);
	}

	public GetSingleLogout(IdentityProviderConfiguration configuration, StageChain stages) {
		super(configuration, stages);
	}

	@Override
	public String getMethod() {
		return "GET";
	}

}
