package com.tazouxme.idp.security.stage.parameters;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;

public class StageParameters {
	
	private IdentityProviderConfiguration configuration;
	private StageRequestParameters requestParameters;
	private StageCookieParameters cookieParameters;
	
	private AuthnRequest authnRequest;
	private LogoutRequest logoutRequest;
	private RequestAbstractType soapRequest;
	private SecretKey secretKey;
	
	private Organization organization;
	private User user;
	private Application application;
	private String redirectUrl;
	
	private StageParameters(IdentityProviderConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public StageParameters(IdentityProviderConfiguration configuration, String redirectUrl) {
		this(configuration);
		this.redirectUrl = redirectUrl;
	}
	
	public StageParameters(IdentityProviderConfiguration configuration, 
			HttpServletRequest request, String organization, String user, String signature) {
		this(configuration);
		this.requestParameters = new StageRequestParameters(request);
		this.cookieParameters = new StageCookieParameters(organization, user, signature);
	}
	
	public StageParameters(IdentityProviderConfiguration configuration, 
			String urlMethod, String urlParam, String samlRequestParam, String relayStateParam, String organization, String user, String signature) {
		this(configuration);
		this.requestParameters = new StageRequestParameters(urlMethod, urlParam, samlRequestParam, relayStateParam);
		this.cookieParameters = new StageCookieParameters(organization, user, signature);
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public HttpServletRequest getRequest() {
		return requestParameters.getRequest();
	}
	
	public String getUrlMethod() {
		return requestParameters.getUrlMethod();
	}
	
	public String getUrlParam() {
		return requestParameters.getUrlParam();
	}

	public String getSamlRequestParam() {
		return requestParameters.getSamlRequestParam();
	}

	public String getRelayStateParam() {
		return requestParameters.getRelayStateParam();
	}

	public String getOrganizationId() {
		return cookieParameters.getOrganization();
	}

	public String getUserId() {
		return cookieParameters.getUser();
	}

	public String getSignature() {
		return cookieParameters.getSignature();
	}
	
	public IdentityProviderConfiguration getConfiguration() {
		return configuration;
	}
	
	public String getIdpDomain() {
		return configuration.getDomain();
	}
	
	public String getIdpPath() {
		return configuration.getPath();
	}
	
	public String getIdpUrn() {
		return configuration.getUrn();
	}
	
	public Credential getPrivateCredential() {
		return configuration.getPrivateCredential();
	}
	
	public BasicX509Credential getPublicCredential() {
		return configuration.getPublicCredential();
	}
	
	public AuthnRequest getAuthnRequest() {
		return authnRequest;
	}
	
	public void setAuthnRequest(AuthnRequest authnRequest) {
		this.authnRequest = authnRequest;
	}
	
	public LogoutRequest getLogoutRequest() {
		return logoutRequest;
	}

	public void setLogoutRequest(LogoutRequest logoutRequest) {
		this.logoutRequest = logoutRequest;
	}
	
	public RequestAbstractType getSoapRequest() {
		return soapRequest;
	}
	
	public void setSoapRequest(RequestAbstractType soapRequest) {
		this.soapRequest = soapRequest;
	}
	
	public SecretKey getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(SecretKey secretKey) {
		this.secretKey = secretKey;
	}
	
	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Application getApplication() {
		return application;
	}
	
	public void setApplication(Application application) {
		this.application = application;
	}

}
