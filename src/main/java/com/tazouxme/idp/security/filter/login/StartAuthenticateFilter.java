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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.IOrganizationBo;
import com.tazouxme.idp.bo.contract.IUserBo;
import com.tazouxme.idp.exception.OrganizationException;
import com.tazouxme.idp.exception.UserException;
import com.tazouxme.idp.model.Organization;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class StartAuthenticateFilter extends AbstractIdentityProviderFilter {

	@Autowired
	private IOrganizationBo organizationBo;
	
	@Autowired
	private IUserBo userBo;
	
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
		
		try {
			if (startAuthentication == null || startAuthentication.getDetails().getParameters() == null ||
					startAuthentication.getDetails().getParameters().getAuthnRequest() == null ||
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
			
			UserAuthenticationToken inAuthentication = new UserAuthenticationToken(user.getExternalId(), user.getPassword());
			inAuthentication.getDetails().setParameters(startAuthentication.getDetails().getParameters());
			inAuthentication.getDetails().getParameters().setSecretKey(generateSharedSecret(keys.getPrivate(), obtainPublicKey(publicKey)));
			inAuthentication.getDetails().setPhase(UserAuthenticationPhase.IS_AUTHENTICATING);
			
			inAuthentication.getDetails().getIdentity().setOrganizationId(organization.getExternalId());
			inAuthentication.getDetails().getIdentity().setOrganization(organization.getCode());
			inAuthentication.getDetails().getIdentity().setUserId(user.getExternalId());
			inAuthentication.getDetails().getIdentity().setEmail(user.getEmail());
			
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
