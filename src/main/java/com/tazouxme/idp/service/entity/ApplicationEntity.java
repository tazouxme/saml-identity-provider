package com.tazouxme.idp.service.entity;

import java.util.HashSet;
import java.util.Set;

public class ApplicationEntity {
	
	private String id;
	private String urn;
	private String name;
	private String description;
	private String acsUrl;
	
	private Set<ClaimEntity> claims = new HashSet<>();
	private Set<AccessEntity> accesses = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
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

	public String getAcsUrl() {
		return acsUrl;
	}

	public void setAcsUrl(String acsUrl) {
		this.acsUrl = acsUrl;
	}
	
	public Set<ClaimEntity> getClaims() {
		return claims;
	}
	
	public void setClaims(Set<ClaimEntity> claims) {
		this.claims = claims;
	}
	
	public Set<AccessEntity> getAccesses() {
		return accesses;
	}
	
	public void setAccesses(Set<AccessEntity> accesses) {
		this.accesses = accesses;
	}

}
