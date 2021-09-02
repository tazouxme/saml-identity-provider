package com.tazouxme.idp.service.entity.base;

public abstract class AbstractEntity {
	
	private String id;
	private int status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}

}
