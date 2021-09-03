package com.tazouxme.idp.security.filter.slo.http;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.stage.chain.StageChain;

public class PostSingleLogout extends AbstractHttpSingleLogout {

	public PostSingleLogout(IdentityProviderConfiguration configuration) {
		super(configuration);
	}

	public PostSingleLogout(IdentityProviderConfiguration configuration, StageChain stages) {
		super(configuration, stages);
	}

	@Override
	public String getMethod() {
		return "POST";
	}

}
