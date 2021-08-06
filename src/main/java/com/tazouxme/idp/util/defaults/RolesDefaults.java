package com.tazouxme.idp.util.defaults;

public enum RolesDefaults {
	
	USER ("http://schemas.tazouxme.com/identity/roles/user", "USER"),
	ADMIN ("http://schemas.tazouxme.com/identity/roles/admin", "ADMIN");
	
	private String uri;
	private String name;
	
	private RolesDefaults(String uri, String name) {
		this.uri = uri;
		this.name = name;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}

}
