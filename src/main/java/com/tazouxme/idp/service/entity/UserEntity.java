package com.tazouxme.idp.service.entity;

import java.util.HashSet;
import java.util.Set;

public class UserEntity {
	
	private String id;
	private String username;
	private String email;
	private boolean enabled;
	private boolean administrator;
	
	private Set<UserDetailsEntity> details = new HashSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}
	
	public Set<UserDetailsEntity> getDetails() {
		return details;
	}
	
	public void setDetails(Set<UserDetailsEntity> details) {
		this.details = details;
	}

}
