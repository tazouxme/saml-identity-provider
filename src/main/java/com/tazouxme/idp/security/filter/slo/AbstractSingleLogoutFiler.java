package com.tazouxme.idp.security.filter.slo;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tazouxme.idp.IdentityProviderConfiguration;
import com.tazouxme.idp.application.contract.IIdentityProviderApplication;
import com.tazouxme.idp.security.filter.AbstractIdentityProviderFilter;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.chain.StageChain;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;

public abstract class AbstractSingleLogoutFiler extends AbstractIdentityProviderFilter {

	@Autowired
	protected IdentityProviderConfiguration configuration;
	
	@Autowired
	protected IIdentityProviderApplication application;
	
	protected StageChain stages;

	public AbstractSingleLogoutFiler(String path, String method) {
		super(new AntPathRequestMatcher(path, method));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.info("Entering SAML SLO Process");
		SecurityContextHolder.clearContext();
		
		try {
			UserAuthenticationToken authentication = obtainAuthenticationAfterCheck(request);
			
			if (UserAuthenticationPhase.USER_ACCESS_VALID.equals(authentication.getDetails().getPhase())) {
				logger.info("User Access is valid");
				// SAML Response
				userAuthenticatedSuccess(request, response, chain, authentication);
				return;
			}
			
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_0701);
		} catch (StageException e) {
			logger.info(e.getType() + ", " + e.getCode());
			
			userAuthenticatedFail(request, response, chain, e);
		}
	}
	
	protected UserAuthenticationToken obtainAuthenticationAfterCheck(HttpServletRequest request) {
		return stages.execute(new UserAuthenticationToken(), obtainStageParameters(request));
	}
	
	protected abstract StageParameters obtainStageParameters(HttpServletRequest request);
	
	protected abstract void userAuthenticatedSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, UserAuthenticationToken authentication)
			throws IOException, ServletException;
	
	protected abstract void userAuthenticatedFail(HttpServletRequest request, HttpServletResponse response, FilterChain chain, StageException e)
			throws IOException, ServletException;
	
	public void setStages(StageChain stages) {
		this.stages = stages;
	}

}