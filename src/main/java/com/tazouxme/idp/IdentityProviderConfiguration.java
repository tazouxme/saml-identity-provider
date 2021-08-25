package com.tazouxme.idp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.security.x509.BasicX509Credential;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Criterion;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

public class IdentityProviderConfiguration implements InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ResourceLoader resourceLoader;
	
	private KeyStore keystore;
	private BasicX509Credential x509;
	
	private String domain;
	private String path;
	private String ssoPath;
	private String ssoSoapPath;
	private String sloPath;
	private String urn;
	private String keystorePath;
	private String keystorePassword;
	private String alias;
	private String keyPassword;
	private String certificatePath;
	
	public IdentityProviderConfiguration() throws InitializationException {
		logger.info("Initializing OpenSAML and BouncyCastle");
		InitializationService.initialize();
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getSsoPath() {
		return ssoPath;
	}
	
	public void setSsoPath(String ssoPath) {
		this.ssoPath = ssoPath;
	}
	
	public String getSsoSoapPath() {
		return ssoSoapPath;
	}
	
	public void setSsoSoapPath(String ssoSoapPath) {
		this.ssoSoapPath = ssoSoapPath;
	}
	
	public String getSloPath() {
		return sloPath;
	}
	
	public void setSloPath(String sloPath) {
		this.sloPath = sloPath;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public void setKeystorePath(String keystorePath) {
		this.keystorePath = keystorePath;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}
	
	public String getCertificatePath() {
		return certificatePath;
	}
	
	public void setCertificatePath(String certificatePath) {
		this.certificatePath = certificatePath;
	}

	public Credential getPrivateCredential() {
		Map<String, String> passwordMap = new HashMap<String, String>();
		passwordMap.put(getAlias(), getKeyPassword());

		KeyStoreCredentialResolver resolver = new KeyStoreCredentialResolver(loadKeyStore(), passwordMap);
		Criterion criterion = new EntityIdCriterion(getAlias());
		CriteriaSet criteriaSet = new CriteriaSet();
		criteriaSet.add(criterion);

		try {
			return resolver.resolveSingle(criteriaSet);
		} catch (ResolverException e) {
			return null;
		}
	}

	public BasicX509Credential getPublicCredential() {
		return loadX509Credential();
	}

	private BasicX509Credential loadX509Credential() {
		try {
			if (x509 == null) {
				logger.info("Loading X509");
				Resource resource = resourceLoader.getResource(getCertificatePath());
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				X509Certificate cert = (X509Certificate) factory.generateCertificate(resource.getInputStream());
				x509 = CredentialSupport.getSimpleCredential(cert, null);
			}
			
			return x509;
		} catch (FileNotFoundException ex) {
			//
		} catch (CertificateException ex) {
			//
		} catch (IOException e) {
			//
		}

		return null;
	}

	private KeyStore loadKeyStore() {
		try {
			if (keystore == null) {
				logger.info("Loading KeyStore");
				keystore = KeyStore.getInstance(KeyStore.getDefaultType());
				Resource resource = resourceLoader.getResource(getKeystorePath());
				keystore.load(resource.getInputStream(), getKeystorePassword().toCharArray());
			}
			
			return keystore;
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong reading keystore", e);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		loadX509Credential();
		loadKeyStore();
	}

}
