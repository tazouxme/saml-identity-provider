package com.tazouxme.idp;

public class IdentityProviderConstants {
	
	public static final String SAML_CLAIM_ORGANIZATION = "http://schemas.tazouxme.com/identity/claims/organization";
	public static final String SAML_CLAIM_EMAIL = "http://schemas.tazouxme.com/identity/claims/email";
	public static final String SAML_CLAIM_ROLE = "http://schemas.tazouxme.com/identity/claims/role";
	public static final String SAML_VERSION = "2.0";
	
	public static final String PARAM_SAML_REQUEST = "SAMLRequest";
	public static final String PARAM_SAML_RELAY_STATE = "RelayState";
	
	public static final String COOKIE_ORGANIZATION = "tz_organization";
	public static final String COOKIE_USER = "tz_user";
	public static final String COOKIE_SIGNATURE = "tz_signature";
	
	public static final String AUTH_HEADER_CSRF = "x-csrf";
	public static final String AUTH_HEADER_ERROR = "x-error";
	public static final String AUTH_HEADER_PUBLIC_KEY = "x-public-key";
	public static final String AUTH_HEADER_ORGANIZATION = "x-organization";
	public static final String AUTH_HEADER_USERNAME = "x-username";

	public static final String AUTH_PARAM_ORGANIZATION = "organization";
	public static final String AUTH_PARAM_USERNAME = "username";
	public static final String AUTH_PARAM_PASSWORD = "password";
	
	public static final String ACTIVATION_CONST_ACTIVATE = "ACTIVATE";
	public static final String ACTIVATION_CONST_PASSWORD = "PASSWORD";
	public static final String ACTIVATION_CONST_RESET = "RESET";
	public static final String ACTIVATION_CONST_ORG = "ORG";
	public static final String ACTIVATION_CONST_USER = "USER";
	public static final String ACTIVATION_PARAM_ACTION = "action";
	public static final String ACTIVATION_PARAM_CODE = "code";
	public static final String ACTIVATION_PARAM_PASSWORD = "password";
	public static final String ACTIVATION_PARAM_PASSWORD_CHECK = "passwordCheck";
	
	public static final String SERVLET_ERROR_WRONG_USER_PASS = "wrongUserPass";
	public static final String SERVLET_ERROR_WRONG_PASS = "wrongPass";
	public static final String SERVLET_ERROR_REGISTER = "registerError";
	public static final String SERVLET_REGISTER_OK = "registerOk";
	
	public static final String SECURITY_SAML_PROCESS = "security-saml-process";

}
