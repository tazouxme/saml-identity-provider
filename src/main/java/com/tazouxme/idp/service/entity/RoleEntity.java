package com.tazouxme.idp.service.entity;

import com.tazouxme.idp.service.entity.base.AbstractEntity;

public class RoleEntity extends AbstractEntity {
	
	private String uri;
	private String name;

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

}
