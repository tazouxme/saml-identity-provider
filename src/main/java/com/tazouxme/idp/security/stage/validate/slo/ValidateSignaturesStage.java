package com.tazouxme.idp.security.stage.validate.slo;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import com.tazouxme.idp.application.exception.SessionException;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateSignaturesStage extends AbstractStage {

	public ValidateSignaturesStage() {
		super(UserAuthenticationPhase.COOKIES_VALID, UserAuthenticationPhase.SIGNATURES_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		try {
			// find Session by user + organization
			String token = idpApplication.findSession(o.getUserId(), o.getOrganizationId()).getToken();
			if (!verifyCookieSignature(token.getBytes(), o.getSignature().getBytes(), o.getPublicCredential().getPublicKey())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0485, o);
			}
			
			logger.info("Signatures valid");
			return authentication;
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0486, o);
		} catch (InvalidKeyException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0482, o);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0483, o);
		} catch (NoSuchAlgorithmException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0484, o);
		}
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}
	
	private boolean verifyCookieSignature(byte[] text, byte[] digitalSignature, PublicKey key) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		Signature signature = generateSignature();
		signature.initVerify(key);
		
		signature.update(text);
		return signature.verify(Base64.decode(digitalSignature));
	}
	
	private Signature generateSignature() throws NoSuchAlgorithmException {
		return Signature.getInstance("SHA256withRSA", new BouncyCastleProvider());
	}

}
