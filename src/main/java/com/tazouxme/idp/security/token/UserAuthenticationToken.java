package com.tazouxme.idp.security.token;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthenticationToken implements Authentication {
	
	private UserAuthenticationDetails details = new UserAuthenticationDetails();
	
	private String username;
	private Object credentials;
	private String role;
	private boolean authenticated = false;
	
	public UserAuthenticationToken() {
		this("", "");
	}
	
	public UserAuthenticationToken(String role) {
		this("", "", role);
	}
	
	public UserAuthenticationToken(String username, Object credentials) {
		this.username = username;
		this.credentials = credentials;
	}
	
	public UserAuthenticationToken(String username, Object credentials, String role) {
		this.username = username;
		this.credentials = credentials;
		this.role = role;
		this.authenticated = true;
	}

	@Override
	public Object getPrincipal() {
		return username;
	}

	@Override
	public String getName() {
		return username;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}
	
	public String getRole() {
		return role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + getRole()));
	}

	@Override
	public UserAuthenticationDetails getDetails() {
		return details;
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException("Cannot set 'isAuthenticated' manually");
		}
	}

}
