package com.tazouxme.idp.security.stage.validate.slo;

import java.util.Set;

import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.springframework.beans.factory.annotation.Autowired;

import com.tazouxme.idp.bo.contract.IAccessBo;
import com.tazouxme.idp.bo.contract.IFederationBo;
import com.tazouxme.idp.exception.AccessException;
import com.tazouxme.idp.exception.FederationException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Federation;
import com.tazouxme.idp.model.User;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.stage.validate.AbstractStage;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserAuthenticationType;
import com.tazouxme.idp.security.token.UserIdentity;

public class ValidateUserAccessStage extends AbstractStage {
	
	public ValidateUserAccessStage() {
		super(UserAuthenticationPhase.ORGANIZATION_ACCESS_VALID, UserAuthenticationPhase.USER_ACCESS_VALID);
	}

	@Autowired
	private IAccessBo accessBo;
	
	@Autowired
	private IFederationBo federationBo;
	
	@Override
	public UserAuthenticationToken executeInternal(UserAuthenticationToken authentication, StageParameters o) throws StageException {
		// find user and check SaaS access
		Access access = null;
		try {
			access = accessBo.findByUserAndURN(o.getUserId(), o.getLogoutRequest().getIssuer().getValue(), o.getOrganizationId());
			if (!access.isEnabled()) {
				throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0681, o);
			}
		} catch (AccessException e) {
			throw new StageException(StageExceptionType.ACCESS, StageResultCode.ACC_0682, o);
		}
		
		User user = o.getUser();
		authentication.getDetails().getIdentity().setUserId(user.getExternalId());
		authentication.getDetails().getIdentity().setEmail(user.getEmail());
		authentication.getDetails().getIdentity().setRole(user.isAdministrator() ? "ADMIN" : "USER");
		
		Set.of(NameIDType.UNSPECIFIED, NameIDType.EMAIL, NameIDType.TRANSIENT, NameIDType.ENCRYPTED, NameIDType.PERSISTENT, NameIDType.ENTITY);
		
		if (NameID.UNSPECIFIED.equals(o.getLogoutRequest().getNameID().getFormat()) || NameID.EMAIL.equals(o.getLogoutRequest().getNameID().getFormat())) {
			if (!o.getLogoutRequest().getNameID().getValue().equals(user.getEmail())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0682, o);
			}
		}
		else if (NameID.ENTITY.equals(o.getLogoutRequest().getNameID().getFormat())) {
			if (!o.getLogoutRequest().getNameID().getValue().equals(access.getApplication().getUrn())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0683, o);
			}
		}
		else if (NameID.TRANSIENT.equals(o.getLogoutRequest().getNameID().getFormat())) {
			if (!o.getLogoutRequest().getNameID().getValue().equals(user.getExternalId())) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0684, o);
			}
		}
		else if (NameID.PERSISTENT.equals(o.getLogoutRequest().getNameID().getFormat())) {
			if (!o.getOrganization().isFederation()) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0685, o);
			}
			
			try {
				Federation federation = federationBo.findByUserAndURN(
						user.getExternalId(), o.getApplication().getUrn(), authentication.getDetails().getIdentity().getOrganizationId());
				
				if (!o.getLogoutRequest().getNameID().getValue().equals(federation.getExternalId())) {
					throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0686, o);
				}
				
				authentication.getDetails().getIdentity().setFederatedUserId(federation.getExternalId());
			} catch (FederationException e) {
				throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0681, o);
			}
		}
		
		UserIdentity identity = authentication.getDetails().getIdentity();
		
		authentication = new UserAuthenticationToken(access.getUser().getExternalId(), "", access.getRole().getName());
		authentication.getDetails().setParameters(o);
		authentication.getDetails().setType(UserAuthenticationType.LOGOUT);
		authentication.getDetails().setResultCode(StageResultCode.OK);
		authentication.getDetails().setIdentity(identity);
		
		logger.info("User access valid");
		return authentication;
	}
	
	@Override
	protected boolean requireEntryPhase() {
		return true;
	}

}
