package com.tazouxme.idp.security.filter.login;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.IApplicationBo;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.exception.ApplicationException;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Application;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

public class StartAuthenticateFilter extends AbstractIdentityProviderFilter {

	@Autowired
	private IOrganizationBo organizationBo;
	
	@Autowired
	private IUserBo userBo;
	
	@Autowired
	private IApplicationBo applicationBo;
	
	public StartAuthenticateFilter() {
		super(new AntPathRequestMatcher("/login", "HEAD"));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String csrf = request.getHeader(IdentityProviderConstants.AUTH_HEADER_CSRF);
		String publicKey = request.getHeader(IdentityProviderConstants.AUTH_HEADER_PUBLIC_KEY);
		String username = request.getHeader(IdentityProviderConstants.AUTH_HEADER_USERNAME);
		
		if (StringUtils.isEmpty(csrf) || StringUtils.isEmpty(publicKey) || StringUtils.isEmpty(username)) {
			logger.error("Empty Header detected");
			response.setStatus(412);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Empty Header detected");
			return;
		}
		
		UserAuthenticationToken startAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		StageParameters parameters = startAuthentication.getDetails().getParameters();
		
		try {
			if (startAuthentication == null || parameters == null ||
					!UserAuthenticationPhase.MUST_AUTHENTICATE.equals(startAuthentication.getDetails().getPhase())) {
				logger.error("Authentication phase is not correct");
				SecurityContextHolder.clearContext();
				
				response.setStatus(406);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Authentication phase is not correct");
				return;
			}
			
			if (!validateUsername(username)) {
				logger.error("Invalid username");
				response.setStatus(404);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Invalid username");
				return;
			}
			
			KeyPair keys = generateKeys();
			Organization organization = organizationBo.findByDomain(username.split("@")[1]);
			User user = userBo.findByEmail(username, organization.getExternalId());
			
			if (!user.isEnabled()) {
				logger.error("User is not active");
				response.setStatus(406);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
				response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "User is not active");
				return;
			}
			
			UserAuthenticationToken inAuthentication = new UserAuthenticationToken(user.getExternalId(), user.getPassword());
			inAuthentication.getDetails().setParameters(parameters);
			inAuthentication.getDetails().getParameters().setOrganization(organization);
			inAuthentication.getDetails().getParameters().setUser(user);
			inAuthentication.getDetails().getParameters().setSecretKey(generateSharedSecret(keys.getPrivate(), obtainPublicKey(publicKey)));
			
			inAuthentication.getDetails().setPhase(UserAuthenticationPhase.IS_AUTHENTICATING);
			inAuthentication.getDetails().setType(startAuthentication.getDetails().getType());
			
			inAuthentication.getDetails().getIdentity().setOrganizationId(organization.getExternalId());
			inAuthentication.getDetails().getIdentity().setOrganization(organization.getCode());
			inAuthentication.getDetails().getIdentity().setUserId(user.getExternalId());
			inAuthentication.getDetails().getIdentity().setEmail(user.getEmail());
			inAuthentication.getDetails().getIdentity().setRole(user.isAdministrator() ? "ADMIN" : "USER");
			
			if (UserAuthenticationType.SAML.equals(startAuthentication.getDetails().getType())) {
				if (StringUtils.isBlank(organization.getPublicKey())) {
					if (NameID.ENCRYPTED.equals(parameters.getAuthnRequest().getNameIDPolicy().getFormat())) {
						logger.error("Organization PublicKey is not set to encrypt NameID");
						response.setStatus(406);
						response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
						response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Organization PublicKey is not set to encrypt NameID");
						return;
					}
					
					if ("POST".equals(parameters.getUrlMethod()) && SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(parameters.getAuthnRequest().getProtocolBinding())) {
						logger.error("Organization PublicKey is not set to verify Signature");
						response.setStatus(406);
						response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
						response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Organization PublicKey is not set to verify Signature");
						return;
					}
				}
				
				// SP Initialized
				Application application = inAuthentication.getDetails().getParameters().getApplication();
				if (application == null) {
					application = applicationBo.findByUrn(parameters.getAuthnRequest().getIssuer().getValue(), organization.getExternalId());
					inAuthentication.getDetails().getParameters().setApplication(application);
				}
				
				if (!application.getAssertionUrl().equals(parameters.getAuthnRequest().getAssertionConsumerServiceURL())) {
					logger.error("Invalid Assertion Consumer Service URL in AuthnRequest");
					response.setStatus(406);
					response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
					response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Invalid Assertion Consumer Service URL in AuthnRequest");
					return;
				}
			}
			
			SecurityContextHolder.getContext().setAuthentication(inAuthentication);

			response.setStatus(202);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_PUBLIC_KEY, new String(Base64.encode(keys.getPublic().getEncoded())));
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ORGANIZATION, organization.getExternalId());
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_USERNAME, user.getExternalId());
		} catch (OrganizationException e) {
			logger.error("Unknown Organization for selected User", e);
			response.setStatus(404);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Unknown Organization for selected User");
		} catch (UserException e) {
			logger.error("Unknown User", e);
			response.setStatus(404);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Unknown User");
		} catch (ApplicationException e) {
			logger.error("Unknown Application");
			response.setStatus(404);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Unknown Application");
		} catch (GeneralSecurityException e) {
			logger.error("Unable to generate the SecretKey from the PublicKey", e);
			response.setStatus(417);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, csrf);
			response.setHeader(IdentityProviderConstants.AUTH_HEADER_ERROR, "Unable to generate the SecretKey from the PublicKey");
		}
	}
	
	private PublicKey obtainPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance("EC", "BC").
			generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
	}
	
	private static KeyPair generateKeys() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
        keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("P-384"));
        
        return keyPairGenerator.generateKeyPair();
    }
	
	private static SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);

        return keyAgreement.generateSecret("AES");
    }
	
	private boolean validateUsername(String username) {
		if (!username.contains("@")) {
			return false;
		}
		
		String[] parts = username.split("@");
		if (StringUtils.isEmpty(parts[0]) || StringUtils.isEmpty(parts[1])) {
			return false;
		}
		
		return true;
	}

}
