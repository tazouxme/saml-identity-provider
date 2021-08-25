package com.tazouxme.idp.service.entity;

public class FederationEntity {
	
	private String id;
	private boolean enabled;
	
	public FederationEntity() {
		this.id = "";
		this.enabled = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
