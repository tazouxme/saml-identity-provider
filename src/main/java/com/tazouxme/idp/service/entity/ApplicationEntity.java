package com.tazouxme.idp.service.entity;

import java.util.HashSet;
import java.util.Set;

import com.tazouxme.idp.service.entity.base.AbstractEntity;

public class ApplicationEntity extends AbstractEntity {
	
	private String urn;
	private String name;
	private String description;
	private String acsUrl;
	private String logoutUrl;
	
	private Set<ClaimEntity> claims = new HashSet<>();
	private Set<AccessEntity> accesses = new HashSet<>();

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
	
	public String getLogoutUrl() {
		return logoutUrl;
	}
	
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
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
