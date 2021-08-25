package com.tazouxme.idp.security.stage.validate.slo;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.saml2.core.NameIDType;

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
		if (StringUtils.isEmpty(o.getLogoutRequest().getID())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0283, o);
		}
		if (StringUtils.isEmpty(o.getLogoutRequest().getIssueInstant().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0285, o);
		}
		if (o.getLogoutRequest().getIssueInstant().getEpochSecond() > new Date().getTime()) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0286, o);
		}
		if (!IdentityProviderConstants.SAML_VERSION.equals(o.getLogoutRequest().getVersion().toString())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0287, o);
		}
		
		if (o.getLogoutRequest().getIssuer() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0289, o);
		}
		if (StringUtils.isEmpty(o.getLogoutRequest().getIssuer().getValue())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0290, o);
		}
		if (!o.getIdpUrn().equals(o.getLogoutRequest().getDestination())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0291, o);
		}

		if (o.getLogoutRequest().getNameID() == null) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0294, o);
		}
		if (StringUtils.isBlank(o.getLogoutRequest().getNameID().getValue())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0295, o);
		}
		
		Set<String> allowedNameIDPolicies = Set.of(NameIDType.UNSPECIFIED, NameIDType.EMAIL, NameIDType.TRANSIENT, NameIDType.ENCRYPTED, NameIDType.PERSISTENT, NameIDType.ENTITY);
		if (!allowedNameIDPolicies.contains(o.getLogoutRequest().getNameID().getFormat())) {
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0296, o);
		}
		
		logger.info("Request values valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
