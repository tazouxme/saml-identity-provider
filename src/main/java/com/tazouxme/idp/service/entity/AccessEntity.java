package com.tazouxme.idp.service.entity;

public class AccessEntity {
	
	private String id;
	private boolean enabled;
	
	private UserEntity user;
	private RoleEntity role;

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
	
	public UserEntity getUser() {
		return user;
	}
	
	public void setUser(UserEntity user) {
		this.user = user;
	}
	
	public RoleEntity getRole() {
		return role;
	}
	
	public void setRole(RoleEntity role) {
		this.role = role;
	}

}
