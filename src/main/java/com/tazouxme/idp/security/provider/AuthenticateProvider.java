package com.tazouxme.idp.security.provider;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.application.exception.AccessException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.security.filter.entity.PasswordEntity;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

public class AuthenticateProvider implements AuthenticationProvider {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private IIdentityProviderApplication application;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		UserAuthenticationToken inAuthentication = (UserAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		UserAuthenticationToken endAuthentication = (UserAuthenticationToken) authentication;
		
		if (!UserAuthenticationPhase.IS_AUTHENTICATING.equals(inAuthentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0901);
		}
		
		// check name are equals
		if (!inAuthentication.getName().equals(endAuthentication.getName())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0902);
		}
		
		try {
			// decrypt password (password in endAuthentication with SecretKey in startAuthentication)
			PasswordEntity passwordEntity = (PasswordEntity) endAuthentication.getCredentials();
			String rawPassword = decrypt(inAuthentication.getDetails().getParameters().getSecretKey(), passwordEntity);

			// bcrypt decrypted password and password in startAuthentication
			String bcryptedPassword = inAuthentication.getCredentials().toString();
			
			if (!BCrypt.checkpw(rawPassword, bcryptedPassword)) {
				throw new StageException(StageExceptionType.CREDENTIALS, StageResultCode.CRE_0901);
			}
		} catch (GeneralSecurityException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0903);
		}
		
		// get and check access
		String role = inAuthentication.getDetails().getIdentity().getRole();
		try {
			if (UserAuthenticationType.SAML.equals(inAuthentication.getDetails().getType())) {
				// SP Initialized
				Access access = application.findAccessByUserAndURN(
					inAuthentication.getDetails().getIdentity().getUserId(), 
					inAuthentication.getDetails().getParameters().getAuthnRequest().getIssuer().getValue(),
					inAuthentication.getDetails().getIdentity().getOrganizationId());
				
				role = access.getRole().getName();
				
				if (!access.isEnabled()) {
					throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0901, inAuthentication.getDetails().getParameters());
				}
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0902, inAuthentication.getDetails().getParameters());
		}
		
		// renew UserAuthenticationToken
		UserAuthenticationToken authenticated = new UserAuthenticationToken(inAuthentication.getName(), "", role);
		authenticated.getDetails().setPhase(UserAuthenticationPhase.IS_AUTHENTICATED);
		authenticated.getDetails().setParameters(inAuthentication.getDetails().getParameters());
		authenticated.getDetails().setResultCode(StageResultCode.OK);
		authenticated.getDetails().setType(inAuthentication.getDetails().getType());
		authenticated.getDetails().setIdentity(inAuthentication.getDetails().getIdentity());
		
		return authenticated;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UserAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
	private static String decrypt(SecretKey key, PasswordEntity encryptedPassword) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING", "BC");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Base64.decode(encryptedPassword.getIv())));
		
		return new String(cipher.doFinal(Base64.decode(encryptedPassword.getPassword())));
    }

}
