package com.tazouxme.idp.security.stage.validate.sso.soap;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.ArtifactResolve;
import org.opensaml.saml.saml2.core.AttributeQuery;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public class ValidateRequestValuesStage extends AbstractStage {

	public ValidateRequestValuesStage() {
		super(UserAuthenticationPhase.REQUEST_PARAMETERS_VALID, UserAuthenticationPhase.REQUEST_VALUES_VALID);
	}
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication,  StageParameters o) throws StageException {
		if (StringUtils.isEmpty(o.getSoapRequest().getID())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0253, o);
		}
		if (StringUtils.isEmpty(o.getSoapRequest().getIssueInstant().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0255, o);
		}
		if (o.getSoapRequest().getIssueInstant().isAfter(Instant.now())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0256, o);
		}
		if (!IdentityProviderConstants.SAML_VERSION.equals(o.getSoapRequest().getVersion().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0257, o);
		}
		if (o.getSoapRequest().getIssuer() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0259, o);
		}
		if (StringUtils.isEmpty(o.getSoapRequest().getIssuer().getValue())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0260, o);
		}
		
		StringBuilder portBuilder = new StringBuilder();
		if (o.getRequest().getServerPort() != 80 && o.getRequest().getServerPort() != 443) {
			portBuilder.append(":");
			portBuilder.append(o.getRequest().getServerPort());
		}
		
		
		if (o.getSoapRequest() instanceof ArtifactResolve) {
			ArtifactResolve artifactResolve = (ArtifactResolve) o.getSoapRequest();
			String entityId = o.getRequest().getScheme() + "://" + o.getConfiguration().getDomain() + portBuilder.toString() + o.getConfiguration().getPath();
			String entitySsoSoapContext = entityId + o.getConfiguration().getSsoSoapPath();
			
			if (!entitySsoSoapContext.equals(artifactResolve.getDestination())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0261, o);
			}
			
			if (artifactResolve.getArtifact() == null) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0262, o);
			}
			if (StringUtils.isEmpty(artifactResolve.getArtifact().getValue())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0263, o);
			}
		} else if (o.getSoapRequest() instanceof AttributeQuery) {
			AttributeQuery attributeQuery = (AttributeQuery) o.getSoapRequest();
			
			if (attributeQuery.getSubject() == null) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0264, o);
			}
			if (attributeQuery.getSubject().getNameID() == null) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0265, o);
			}
			if (attributeQuery.getAttributes() == null || attributeQuery.getAttributes().isEmpty()) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0266, o);
			}
		} else {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0267, o);
		}
		
		logger.info("Request values valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
