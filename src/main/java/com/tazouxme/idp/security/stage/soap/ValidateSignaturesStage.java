package com.tazouxme.idp.security.stage.soap;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.security.stage.AbstractStage;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateSignaturesStage extends AbstractStage {

	public ValidateSignaturesStage() {
		super(UserAuthenticationPhase.COOKIES_VALID, UserAuthenticationPhase.SIGNATURES_VALID);
	}

	@Autowired
	private ISessionBo bo;
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		try {
			// find Session by user + organization
			String token = bo.find(o.getOrganizationId(), o.getUserId()).getToken();
			if (!verifyCookieSignature(token.getBytes(), o.getSignature().getBytes(), o.getPublicCredential().getPublicKey())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0455, o);
			}
			
			logger.info("Signatures valid");
			return authentication;
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0456, o);
		} catch (InvalidKeyException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0452, o);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0453, o);
		} catch (NoSuchAlgorithmException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0454, o);
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
