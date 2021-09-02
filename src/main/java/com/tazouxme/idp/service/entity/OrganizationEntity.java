package com.tazouxme.idp.service.entity;

import java.util.HashSet;
import java.util.Set;

public class OrganizationEntity {
	
	private String id;
	private String code;
	private String domain;
	private String name;
	private String description;
	private boolean federation;
	private long creationDate;
	private Set<ClaimEntity> claims = new HashSet<>();
	private Set<RoleEntity> roles = new HashSet<>();
	private String certificate;
	private boolean hasCertificate = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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
	
	public boolean isFederation() {
		return federation;
	}
	
	public void setFederation(boolean federation) {
		this.federation = federation;
	}
	
	public String getCertificate() {
		return certificate;
	}
	
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	
	public boolean isHasCertificate() {
		return hasCertificate;
	}
	
	public void setHasCertificate(boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	
	public Set<ClaimEntity> getClaims() {
		return claims;
	}
	
	public void setClaims(Set<ClaimEntity> claims) {
		this.claims = claims;
	}
	
	public Set<RoleEntity> getRoles() {
		return roles;
	}
	
	public void setRoles(Set<RoleEntity> roles) {
		this.roles = roles;
	}

}
