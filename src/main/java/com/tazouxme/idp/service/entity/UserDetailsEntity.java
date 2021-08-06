package com.tazouxme.idp.service.entity;

public class UserDetailsEntity {
	
	private ClaimEntity claim;
	private String claimValue;
	private long creationDate;

	public ClaimEntity getClaim() {
		return claim;
	}

	public void setClaim(ClaimEntity claim) {
		this.claim = claim;
	}

	public String getClaimValue() {
		return claimValue;
	}

	public void setClaimValue(String claimValue) {
		this.claimValue = claimValue;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

}
