package com.tazouxme.idp.service.entity;

import com.tazouxme.idp.service.entity.base.AbstractEntity;

public class FederationEntity extends AbstractEntity {
	
	private boolean enabled;
	
	public FederationEntity() {
		this.enabled = false;
		setId("");
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
