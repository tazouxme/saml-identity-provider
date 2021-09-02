package com.tazouxme.idp.service.entity;

import com.tazouxme.idp.service.entity.base.AbstractEntity;

public class ClaimEntity extends AbstractEntity {
	
	private String uri;
	private String name;
	private String description;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
