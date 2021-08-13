package com.tazouxme.idp.security.stage;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.net.URLBuilder;

public class ValidateSignaturesStage implements Stage {

	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private ISessionBo bo;
	
	@Override
	public UserAuthenticationToken execute(UserAuthenticationToken authentication, 
			StageParameters o) throws StageException {
		if (!UserAuthenticationPhase.COOKIES_VALID.equals(authentication.getDetails().getPhase())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0401, o);
		}
		
		try {
			if (NameID.ENCRYPTED.equals(o.getAuthnRequest().getNameIDPolicy().getFormat())) {
				if (StringUtils.isBlank(o.getOrganization().getPublicKey())) {
					throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0415, o);
				}
			}
			
			// verify SAML Signature
			if ("POST".equals(o.getUrlMethod()) && SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI.equals(o.getAuthnRequest().getProtocolBinding())) {
				if (!verifyRequestSignature(o)) {
					throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0405, o);
				}
			}
			
			// find Session by user + organization
			String token = bo.find(o.getOrganizationId(), o.getUserId()).getToken();
			if (!verifyCookieSignature(token.getBytes(), o.getSignature().getBytes(), o.getPublicCredential().getPublicKey())) {
				throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0401, o);
			}
			
			logger.info("Signatures valid");
			
			authentication.getDetails().setPhase(UserAuthenticationPhase.SIGNATURES_VALID);
			return authentication;
		} catch (SessionException e) {
			throw new StageException(StageExceptionType.AUTHENTICATION, StageResultCode.AUT_0402, o);
		} catch (InvalidKeyException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0402, o);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0403, o);
		} catch (NoSuchAlgorithmException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0404, o);
		}
	}
	
	private boolean verifyCookieSignature(byte[] text, byte[] digitalSignature, PublicKey key) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		Signature signature = generateSignature();
		signature.initVerify(key);
		
		signature.update(text);
		return signature.verify(Base64.decode(digitalSignature));
	}
	
	private boolean verifyRequestSignature(StageParameters o) {
		if (StringUtils.isBlank(o.getOrganization().getPublicKey())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0414, o);
		}
		
		try {
			URLBuilder incomingUrl = new URLBuilder(o.getUrlParam());
			List<Pair<String, String>> incomingParams = incomingUrl.getQueryParams();
			
			URLBuilder verifierUrl = new URLBuilder(o.getUrlParam());
			List<Pair<String, String>> verifierParams = verifierUrl.getQueryParams();
		
			verifierParams.add(new Pair<>("SAMLRequest", obtainQueryParam("SAMLRequest", incomingParams, o)));
			verifierParams.add(new Pair<>("RelayState", obtainQueryParam("RelayState", incomingParams, o)));
			verifierParams.add(new Pair<>("SigAlg", obtainQueryParam("SigAlg", incomingParams, o)));
			
			byte[] signaturesBytes = Base64.decode(obtainQueryParam("Signature", incomingParams, o));
		
			Signature sig = Signature.getInstance("SHA256withRSA");
			sig.initVerify(obtainPublicKey(o.getOrganization().getPublicKey()));
			sig.update(verifierUrl.buildQueryString().getBytes("UTF-8"));
			return sig.verify(signaturesBytes);
		} catch (SignatureException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0406, o);
		} catch (InvalidKeyException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0407, o);
		} catch (NoSuchAlgorithmException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0408, o);
		} catch (UnsupportedEncodingException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0409, o);
		} catch (InvalidKeySpecException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0410, o);
		} catch (NoSuchProviderException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0411, o);
		} catch (MalformedURLException e) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0412, o);
		}
	}
	
	private String obtainQueryParam(String param, List<Pair<String, String>> incomingParams, StageParameters o) {
		for (Pair<String, String> incomingParam : incomingParams) {
			if (param.equals(incomingParam.getFirst())) {
				return incomingParam.getSecond();
			}
		}
		
		throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0413, o);
	}
	
	private PublicKey obtainPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance("RSA", "BC").
			generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
	}
	
	private Signature generateSignature() throws NoSuchAlgorithmException {
		return Signature.getInstance("SHA256withRSA", new BouncyCastleProvider());
	}

}
