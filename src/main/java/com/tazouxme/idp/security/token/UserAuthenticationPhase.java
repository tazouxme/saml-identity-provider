package com.tazouxme.idp.security.token;

public enum UserAuthenticationPhase {
	
	REQUEST_PARAMETERS_VALID,
	REQUEST_URL_VALID,
	REQUEST_VALUES_VALID,
	COOKIES_VALID,
	SIGNATURES_VALID,
	ORGANIZATION_ACCESS_VALID,
	USER_ACCESS_VALID,
	
	SLO_FAILED,
	SSO_FAILED,
	
	MUST_AUTHENTICATE,
	MUST_ACTIVATE,
	IS_AUTHENTICATING,
	IS_AUTHENTICATED,
	
	WEB_PAGE_ALLOWED,
	WEB_PAGE_ACCESS;

}
