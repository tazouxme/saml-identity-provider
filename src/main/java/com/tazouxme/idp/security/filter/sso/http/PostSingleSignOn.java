package com.tazouxme.idp.security.filter.sso.http;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.security.stage.chain.StageChain;

public class PostSingleSignOn extends AbstractHttpSingleSignOn {

	public PostSingleSignOn(IdentityProviderConfiguration configuration) {
		super(configuration);
	}

	public PostSingleSignOn(IdentityProviderConfiguration configuration, StageChain stages) {
		super(configuration, stages);
	}

	@Override
	public String getMethod() {
		return "POST";
	}

}
