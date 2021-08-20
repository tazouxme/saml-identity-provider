package com.tazouxme.idp.security.token;

import java.util.HashMap;
import java.util.Map;

public class UserIdentity {
	
	private String organizationId;
	private String organization;
	private String federatedUserId;
	private String userId;
	private String email;
	private String role;
	private String token;
	private Map<String, String> claims = new HashMap<>();

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getFederatedUserId() {
		return federatedUserId;
	}
	
	public void setFederatedUserId(String federatedUserId) {
		this.federatedUserId = federatedUserId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Map<String, String> getClaims() {
		return claims;
	}
	
	public void setClaims(Map<String, String> claims) {
		this.claims = claims;
	}

}
