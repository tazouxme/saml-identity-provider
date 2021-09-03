package com.tazouxme.idp.security.entity;

public class PasswordEntity {
	
	private String password;
	private String iv;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

}
