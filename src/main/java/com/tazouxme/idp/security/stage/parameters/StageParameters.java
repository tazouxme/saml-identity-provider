package com.tazouxme.idp.security.stage.parameters;

import javax.crypto.SecretKey;

import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;

public class StageParameters {
	
	private IdentityProviderConfiguration configuration;
	private StageRequestParameters requestParameters;
	private StageCookieParameters cookieParameters;
	
	private AuthnRequest authnRequest;
	private SecretKey secretKey;
	
	private Organization organization;
	private User user;
	
	public StageParameters(IdentityProviderConfiguration configuration, 
			String urlMethod, String urlParam, String samlRequestParam, String relayStateParam, String organization, String user, String signature) {
		this.configuration = configuration;
		this.requestParameters = new StageRequestParameters(urlMethod, urlParam, samlRequestParam, relayStateParam);
		this.cookieParameters = new StageCookieParameters(organization, user, signature);
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

}
