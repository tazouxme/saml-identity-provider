package com.tazouxme.idp.security.filter.handler;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.client.HttpClientBuilder;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.pipeline.httpclient.BasicHttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.binding.decoding.impl.HttpClientResponseSOAP11Decoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HttpClientRequestSOAP11Encoder;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.http.AbstractPipelineHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.bo.contract.ISessionBo;
import com.tazouxme.idp.exception.SessionException;
import com.tazouxme.idp.model.Access;
import com.tazouxme.idp.model.Session;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.idp.security.stage.exception.StageException;
import com.tazouxme.idp.security.stage.exception.StageExceptionType;
import com.tazouxme.idp.security.stage.parameters.StageParameters;
import com.tazouxme.idp.security.token.UserAuthenticationPhase;
import com.tazouxme.idp.security.token.UserAuthenticationToken;
import com.tazouxme.idp.security.token.UserIdentity;
import com.tazouxme.idp.security.velocity.IdpVelocityEngine;
import com.tazouxme.idp.util.CookieUtils;
import com.tazouxme.idp.util.SAMLUtils;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

public class LogoutAuthenticationHandler extends AbstractAuthenticationHandler {
	
	public LogoutAuthenticationHandler(ApplicationContext context) {
		super(context);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		StageParameters parameters = authentication.getDetails().getParameters();
		LogoutRequest logoutRequest = parameters.getLogoutRequest();
		
		if (logoutRequest == null) {
			logger.error("'logoutRequest' parameter is null");
			fault(request, response, authentication);
			return;
		}
		
		boolean success = !UserAuthenticationPhase.SLO_FAILED.equals(authentication.getDetails().getPhase());
		if (success) {
			for (Access access : authentication.getDetails().getParameters().getUser().getAccesses()) {
				try {
					// send SOAP to all SaaS
					LogoutResponse logoutResponse = logout(request, response, authentication, access);
					
					if (logoutResponse != null && StatusCode.SUCCESS.equals(logoutResponse.getStatus().getStatusCode().getValue())) {
						continue;
					}
				} catch (StageException e) {
					logger.error("logoutResponse is null or StatusCode is not Success");
					fault(request, response, authentication);
					return;
				}
			}
			
			if (disconnect(authentication.getDetails().getIdentity())) {
				CookieUtils.delete(response, CookieUtils.create(IdentityProviderConstants.COOKIE_ORGANIZATION, "", getConfiguration().getDomain(), getConfiguration().getPath(), 0));
				CookieUtils.delete(response, CookieUtils.create(IdentityProviderConstants.COOKIE_USER, "", getConfiguration().getDomain(), getConfiguration().getPath(), 0));
				CookieUtils.delete(response, CookieUtils.create(IdentityProviderConstants.COOKIE_SIGNATURE, "", getConfiguration().getDomain(), getConfiguration().getPath(), 0));
				
				SecurityContextHolder.clearContext();
				
				response.sendRedirect("./dashboard");
				return;
			}
			
		}
		
		fault(request, response, authentication);
	}
	
	@Override
	public void fault(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication) throws IOException, ServletException {
		// send POST LogoutResponse unsuccessful
		Response samlResponse = SAMLUtils.buildResponse(
				authentication.getDetails().getParameters(),
				authentication.getDetails().getIdentity(),
				authentication.getRole(),
				authentication.getDetails().getResultCode(),
				authentication.getDetails().getParameters().getLogoutRequest().getID());
		
		MessageContext messageContext = new MessageContext();
		messageContext.setMessage(samlResponse);

		messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(authentication.getDetails().getParameters().getRelayStateParam());
		messageContext.getSubcontext(SAMLPeerEntityContext.class, true).getSubcontext(SAMLEndpointContext.class, true).
				setEndpoint(SAMLUtils.getEndpoint(authentication.getDetails().getParameters().getApplication().getLogoutUrl()));
		
		HTTPPostEncoder encoder = new HTTPPostEncoder();
		encoder.setVelocityEngine(new IdpVelocityEngine());
		encoder.setVelocityTemplateId("/velocity/post-saml.vm");
		encoder.setMessageContext(messageContext);
		encoder.setHttpServletResponse(response);
		
		try {
			encoder.prepareContext();
			encoder.initialize();
			encoder.encode();
		} catch (MessageEncodingException e1) {
			request.setAttribute("code", authentication.getDetails().getResultCode().getCode());
			request.setAttribute("reason", authentication.getDetails().getResultCode().getReason());
			request.setAttribute("status", authentication.getDetails().getResultCode().getStatus());
			
			request.getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		} catch (ComponentInitializationException e1) {
			request.setAttribute("code", authentication.getDetails().getResultCode().getCode());
			request.setAttribute("reason", authentication.getDetails().getResultCode().getReason());
			request.setAttribute("status", authentication.getDetails().getResultCode().getStatus());
			
			request.getRequestDispatcher("/error.jsp").forward(request, response);
			return;
		}
	}
	
	private LogoutResponse logout(HttpServletRequest request, HttpServletResponse response, UserAuthenticationToken authentication, Access access) throws StageException {
		SignatureSigningParameters signatureSigningParameters = new SignatureSigningParameters();
		signatureSigningParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signatureSigningParameters.setSigningCredential(getConfiguration().getPrivateCredential());
		signatureSigningParameters.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

		MessageContext contextout = new MessageContext();
		contextout.setMessage(SAMLUtils.buildLogoutRequest(
				authentication.getDetails().getParameters().getLogoutRequest().getNameID(), 
				getConfiguration().getUrn(), 
				access.getApplication().getLogoutUrl(), 
				getConfiguration().getPrivateCredential()));
		contextout.getSubcontext(SecurityParametersContext.class, true).setSignatureSigningParameters(signatureSigningParameters);

		InOutOperationContext context = new ProfileRequestContext();
		context.setOutboundMessageContext(contextout);

		AbstractPipelineHttpSOAPClient soapClient = new AbstractPipelineHttpSOAPClient() {

			@Override
			@Nonnull
			protected HttpClientMessagePipeline newPipeline() throws SOAPException {
				BasicHttpClientMessagePipeline pipeline = new BasicHttpClientMessagePipeline(new HttpClientRequestSOAP11Encoder(), new HttpClientResponseSOAP11Decoder());
				pipeline.setOutboundPayloadHandler(new SAMLOutboundProtocolMessageSigningHandler());

				return pipeline;
			}
		};

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		soapClient.setHttpClient(clientBuilder.build());
		
		try {
			soapClient.send(access.getApplication().getLogoutUrl(), context);
		} catch (SOAPException | SecurityException e) {
			logger.error("SOAP request not sent", e);
			authentication.getDetails().setResultCode(StageResultCode.FAT_1501);
			
			throw new StageException(StageExceptionType.FATAL, StageResultCode.FAT_1501, authentication.getDetails().getParameters());
		}

		return (LogoutResponse) context.getInboundMessageContext().getMessage();
	}
	
	private boolean disconnect(UserIdentity identity) {
		ISessionBo sessionBo = getContext().getBean(ISessionBo.class);
		
		try {
			Session session = sessionBo.find(identity.getUserId(), identity.getOrganizationId());
			sessionBo.delete(session);
		} catch (SessionException e) {
			logger.error("Cannot delete the Session", e);
			return false;
		}
		
		return true;
	}

}
