package com.tazouxme.idp.util.defaults;

public enum ClaimsDefaults {
	
	ORGANIZATION ("http://schemas.tazouxme.com/identity/claims/organization", "ORG", "Organization"),
	EMAIL ("http://schemas.tazouxme.com/identity/claims/email", "EMAIL", "E-Mail"),
	FIRSTNAME ("http://schemas.tazouxme.com/identity/claims/firstname", "FIRSTNAME", "Firstname"),
	LASTNAME ("http://schemas.tazouxme.com/identity/claims/lastname", "LASTNAME", "Lastname"),
	ADDRESS ("http://schemas.tazouxme.com/identity/claims/address", "ADDRESS", "Address"),
	COUNTRY ("http://schemas.tazouxme.com/identity/claims/country", "COUNTRY", "Country"),
	CITY ("http://schemas.tazouxme.com/identity/claims/city", "CITY", "City"),
	ZIP_CODE ("http://schemas.tazouxme.com/identity/claims/zipCode", "ZIPCODE", "ZIP Code"),
	SEX ("http://schemas.tazouxme.com/identity/claims/sex", "SEX", "Sex"),
	DATE_OF_BIRTH ("http://schemas.tazouxme.com/identity/claims/birthdate", "BIRTHDATE", "Date of Birth");
	
	private String uri;
	private String name;
	private String description;
	
	private ClaimsDefaults(String uri, String name, String description) {
		this.uri = uri;
		this.name = name;
		this.description = description;
	}
	
	public String getUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

}
