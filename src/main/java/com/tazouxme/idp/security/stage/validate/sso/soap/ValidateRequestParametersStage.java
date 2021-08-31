package com.tazouxme.idp.security.stage.validate.sso.soap;

import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPSOAP11Decoder;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.AttributeQuery;
import org.opensaml.saml.saml2.core.RequestAbstractType;

import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;

public class ValidateRequestParametersStage extends AbstractStage {

	public ValidateRequestParametersStage() {
		super(null, UserAuthenticationPhase.REQUEST_PARAMETERS_VALID);
	}
	
	@Override
	protected UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		authentication.getDetails().setType(UserAuthenticationType.SOAP);
		
		HTTPSOAP11Decoder decoder = new HTTPSOAP11Decoder();
        decoder.setHttpServletRequest(o.getRequest());
        
        Object message = null;
        
        try {
            BasicParserPool parserPool = new BasicParserPool();
            parserPool.initialize();
            decoder.setParserPool(parserPool);
            decoder.initialize();
            decoder.decode();
        
	        message = decoder.getMessageContext().getMessage();
        } catch (MessageDecodingException e) {
        	throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0151);
        } catch (ComponentInitializationException e) {
        	throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0152);
        }
        
        if (!(message instanceof ArtifactResolve) && !(message instanceof AttributeQuery)) {
        	throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0153);
        }
        
        o.setSoapRequest((RequestAbstractType) message);
		
		logger.info("Request parameters valid");
		
		authentication.getDetails().setParameters(o);
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return false;
	}

}
