package com.tazouxme.idp.util;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;

public class MetadataUtils {

	public static EntityDescriptor buildMetadata(String entity, String ssoContext, String ssoSoapContext, String sloContext, Credential credential) {
		IDPSSODescriptor idpDescription = SAMLUtils.buildSAMLObject(IDPSSODescriptor.class);
		idpDescription.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		idpDescription.setWantAuthnRequestsSigned(Boolean.FALSE);
		idpDescription.getKeyDescriptors().add(buildKeyDescriptor(credential));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.EMAIL));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.ENCRYPTED));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.ENTITY));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.PERSISTENT));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.TRANSIENT));
		idpDescription.getNameIDFormats().add(buildNameIDFormat(NameID.UNSPECIFIED));
		idpDescription.getArtifactResolutionServices().add(buildArtifactorResolutionService(SAMLConstants.SAML2_SOAP11_BINDING_URI, ssoSoapContext));
		idpDescription.getSingleSignOnServices().add(buildSingleSignOnService(SAMLConstants.SAML2_ARTIFACT_BINDING_URI, ssoContext));
		idpDescription.getSingleSignOnServices().add(buildSingleSignOnService(SAMLConstants.SAML2_REDIRECT_BINDING_URI, ssoContext));
		idpDescription.getSingleSignOnServices().add(buildSingleSignOnService(SAMLConstants.SAML2_POST_BINDING_URI, ssoContext));
		idpDescription.getSingleSignOnServices().add(buildSingleSignOnService(SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI, ssoContext));
		idpDescription.getSingleLogoutServices().add(buildSingleLogoutService(SAMLConstants.SAML2_REDIRECT_BINDING_URI, sloContext));
		idpDescription.getSingleLogoutServices().add(buildSingleLogoutService(SAMLConstants.SAML2_POST_BINDING_URI, sloContext));

		EntityDescriptor entityDescriptor = SAMLUtils.buildSAMLObject(EntityDescriptor.class);
		entityDescriptor.setEntityID(entity);
		entityDescriptor.getRoleDescriptors().add(idpDescription);

		return entityDescriptor;
	}

	private static NameIDFormat buildNameIDFormat(String uri) {
		NameIDFormat nameIDFormat = SAMLUtils.buildSAMLObject(NameIDFormat.class);
		nameIDFormat.setURI(uri);

		return nameIDFormat;
	}
	
	private static SingleLogoutService buildSingleLogoutService(String binding, String ctx) {
		SingleLogoutService sloService = SAMLUtils.buildSAMLObject(SingleLogoutService.class);
		sloService.setBinding(binding);
		sloService.setLocation(ctx);
		
		return sloService;
	}
	
	private static SingleSignOnService buildSingleSignOnService(String binding, String ctx) {
		SingleSignOnService ssoService = SAMLUtils.buildSAMLObject(SingleSignOnService.class);
		ssoService.setBinding(binding);
		ssoService.setLocation(ctx);
		
		return ssoService;
	}
	
	private static ArtifactResolutionService buildArtifactorResolutionService(String binding, String ctx) {
		ArtifactResolutionService ssoService = SAMLUtils.buildSAMLObject(ArtifactResolutionService.class);
		ssoService.setBinding(binding);
		ssoService.setIndex(0);
		ssoService.setLocation(ctx);
		
		return ssoService;
	}

	private static KeyDescriptor buildKeyDescriptor(Credential credential) {
		X509KeyInfoGeneratorFactory keyInfoGeneratorFactory = new X509KeyInfoGeneratorFactory();
		keyInfoGeneratorFactory.setEmitEntityCertificate(true);
		KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();

		KeyDescriptor signKeyDescriptor = SAMLUtils.buildSAMLObject(KeyDescriptor.class);
		signKeyDescriptor.setUse(UsageType.SIGNING);

		try {
			signKeyDescriptor.setKeyInfo(keyInfoGenerator.generate(credential));
			return signKeyDescriptor;
		} catch (SecurityException e) {
			return null;
		}
	}

}
