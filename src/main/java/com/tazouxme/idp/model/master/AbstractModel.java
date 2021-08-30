package com.tazouxme.idp.model.master;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractModel implements Serializable {
	
	@Column(name = "created_by", length = 16, updatable = false, nullable = false)
	private String createdBy;
	
	@Column(name = "creation_date", length = 16, updatable = false, nullable = false)
	private long creationDate;
	
	@Column(name = "status", length = 2, nullable = false)
	private int status;
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
