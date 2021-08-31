package com.tazouxme.saml.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.Nonnull;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.pipeline.httpclient.BasicHttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.decoding.impl.HttpClientResponseSOAP11Decoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HttpClientRequestSOAP11Encoder;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.soap.client.http.AbstractPipelineHttpSOAPClient;
import org.opensaml.soap.common.SOAPException;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.filter.entity.PasswordEntity;
import com.tazouxme.idp.security.stage.StageResultCode;
import com.tazouxme.saml.test.util.SAMLUtilsTest;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.net.URLBuilder;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

public class ClientAttributeQueryTest extends AbstractClientTest {

	private static final String SSO_URL = "http://localhost:20126/saml-identity-provider/sso";
	private static final String SOAP_URL = "http://localhost:20126/saml-identity-provider/sso/soap";
	private static final String SSO_URN = "urn:com:tazouxme:idp";
	private static final String SSO_ACS1 = "http://localhost/test1/acs";
	private static final String SSO_APP1 = "urn:com:tazouxme:test1";
	private static final String SSO_APP2 = "urn:com:tazouxme:test2";
	private static final String SSO_RELAY_STATE = UUID.randomUUID().toString();

	private static final String LOGIN_URL = "http://localhost:20126/saml-identity-provider/login";

	@Test
	@Order(1)
	public void testAttributeQuerySuccess() {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpGet get = new HttpGet(buildAuthnQueryParams(SSO_RELAY_STATE, SSO_URN, SSO_ACS1, SSO_APP1, SAMLConstants.SAML2_REDIRECT_BINDING_URI));
			CloseableHttpResponse loginPage = httpClient.execute(get); // -- User is redirected to Login page
			assertEquals(200, loginPage.getStatusLine().getStatusCode());

			String text = new BufferedReader(
					new InputStreamReader(loginPage.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Login"));

			KeyPair keys = generateKeys();

			HttpHead head = new HttpHead(LOGIN_URL);
			head.setHeader(IdentityProviderConstants.AUTH_HEADER_USERNAME, "user1@test.com");
			head.setHeader(IdentityProviderConstants.AUTH_HEADER_CSRF, "csrf");
			head.setHeader(IdentityProviderConstants.AUTH_HEADER_PUBLIC_KEY,
					new String(Base64.encode(keys.getPublic().getEncoded())));

			CloseableHttpResponse loginUser = httpClient.execute(head); // -- User gets Organization, User and PublicKey information for login
			assertEquals(202, loginUser.getStatusLine().getStatusCode());

			String orgId = loginUser.getFirstHeader(IdentityProviderConstants.AUTH_HEADER_ORGANIZATION).getValue();
			String userId = loginUser.getFirstHeader(IdentityProviderConstants.AUTH_HEADER_USERNAME).getValue();
			String csrf = loginUser.getFirstHeader(IdentityProviderConstants.AUTH_HEADER_CSRF).getValue();
			String publicKey = loginUser.getFirstHeader(IdentityProviderConstants.AUTH_HEADER_PUBLIC_KEY).getValue();

			assertEquals("csrf", csrf);

			SecretKey secretKey = generateSharedSecret(keys.getPrivate(), obtainPublicKey(publicKey));
			PasswordEntity password = encryptPassword(secretKey, "pass");

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", orgId));
			params.add(new BasicNameValuePair("username", userId));
			params.add(new BasicNameValuePair("keepalive", "on"));
			params.add(new BasicNameValuePair("password", "{\"password\":" + "\"" + password.getPassword() + "\","
					+ "\"iv\":" + "\"" + password.getIv() + "\"}"));

			HttpPost post = new HttpPost(LOGIN_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse loggedinUser = httpClient.execute(post); // -- User is now logged in
			assertEquals(200, loggedinUser.getStatusLine().getStatusCode());

			Set<String> cookieHeaders = Arrays.asList(loggedinUser.getAllHeaders()).stream()
					.filter(header -> header.getName().equals("Set-Cookie")).map(header -> header.getValue())
					.collect(Collectors.toSet());

			assertTrue(cookieHeaders.size() == 3);
			
			String signature = null;
			for (String cookieHeader : cookieHeaders) {
				if (cookieHeader.contains("signature")) {
					signature = cookieHeader.split(";")[0].trim().replace(IdentityProviderConstants.COOKIE_SIGNATURE + "=", "");
				}
			}
			
			final String signatureFinal = signature;

			text = new BufferedReader(new InputStreamReader(loggedinUser.getEntity().getContent(), StandardCharsets.UTF_8))
					.lines().collect(Collectors.joining("\n"));

			assertTrue(text.contains("RelayState"));
			assertEquals(SSO_RELAY_STATE, findInputValue(text, "RelayState"));
			
			assertTrue(text.contains("SAMLResponse"));
			StatusResponseType response = SAMLUtilsTest.getResponse(Base64.decode(findInputValue(text, "SAMLResponse")));
			assertEquals(StageResultCode.OK.getStatus(), response.getStatus().getStatusCode().getValue());
			
			MessageContext contextout = new MessageContext();
			contextout.setMessage(SAMLUtilsTest.buildAttributeQuery(SSO_URN, SSO_APP2, "USE_user1", "http://schemas.tazouxme.com/identity/claims/organization"));

			InOutOperationContext context = new ProfileRequestContext();
			context.setOutboundMessageContext(contextout);
			
			AbstractPipelineHttpSOAPClient soapClient = new AbstractPipelineHttpSOAPClient() {

				@Override
				@Nonnull
				protected HttpClientMessagePipeline newPipeline() throws SOAPException {
					return new BasicHttpClientMessagePipeline(new HttpClientRequestSOAP11Encoder(), new HttpClientResponseSOAP11Decoder());
				}
				
				@Override
				protected HttpClientContext resolveClientContext(org.opensaml.messaging.context.InOutOperationContext operationContext) {
					BasicCookieStore cookieStore = new BasicCookieStore();
				    cookieStore.addCookie(obtainCookie(IdentityProviderConstants.COOKIE_ORGANIZATION, "ORG_test"));
				    cookieStore.addCookie(obtainCookie(IdentityProviderConstants.COOKIE_USER, "USE_user1"));
				    cookieStore.addCookie(obtainCookie(IdentityProviderConstants.COOKIE_SIGNATURE, signatureFinal));
				    
				    HttpClientContext localContext = new HttpClientContext();
				    localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
				    
				    return localContext;
				};
				
			};
			soapClient.setHttpClient(HttpClients.createDefault());
			soapClient.send(SOAP_URL, context);
			
			Response r = (Response) context.getInboundMessageContext().getMessage();
			assertEquals(StageResultCode.OK.getStatus(), response.getStatus().getStatusCode().getValue());
			assertEquals(1, r.getAssertions().get(0).getAttributeStatements().get(0).getAttributes().size());
		} catch (Exception e) {
			fail(e);
		}
	}

	private String buildAuthnQueryParams(String relayState, String ssoUrn, String ssoAcs, String application, String binding) throws Exception {
		final String messageStr = SerializeSupport.nodeToString(
				XMLObjectSupport.marshall(SAMLUtilsTest.buildHttpAuthnRequest(ssoUrn, ssoAcs, application, AuthnContext.PASSWORD_AUTHN_CTX, binding, NameIDType.EMAIL)));

		try (final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
				final DeflaterOutputStream deflaterStream = new NoWrapAutoEndDeflaterOutputStream(bytesOut,
						Deflater.DEFLATED)) {

			deflaterStream.write(messageStr.getBytes("UTF-8"));
			deflaterStream.finish();

			String deflatedAuthnRequest = Base64Support.encode(bytesOut.toByteArray(), Base64Support.UNCHUNKED);

			URLBuilder urlBuilder = null;
			try {
				urlBuilder = new URLBuilder(SSO_URL);
			} catch (final MalformedURLException e) {
				throw new MessageEncodingException("Endpoint URL " + SSO_URL + " is not a valid URL", e);
			}

			final List<Pair<String, String>> queryParams = urlBuilder.getQueryParams();
			queryParams.add(new Pair<>("SAMLRequest", deflatedAuthnRequest));
			queryParams.add(new Pair<>("RelayState", relayState));

			return urlBuilder.buildURL();
		}
	}

	private static KeyPair generateKeys()
			throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");
		keyPairGenerator.initialize(ECNamedCurveTable.getParameterSpec("P-384"));

		return keyPairGenerator.generateKeyPair();
	}

	private static SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey publicKey)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
		KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
		keyAgreement.init(privateKey);
		keyAgreement.doPhase(publicKey, true);

		return keyAgreement.generateSecret("AES");
	}

	private PublicKey obtainPublicKey(String publicKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		return KeyFactory.getInstance("EC", "BC").generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
	}

	private PasswordEntity encryptPassword(SecretKey key, String pass) throws Exception {
		byte[] iv = generateRandomBytes(16);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING", new BouncyCastleProvider());
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		String encryptedData = new String(Base64.encode(cipher.doFinal(pass.getBytes())));

		PasswordEntity e = new PasswordEntity();
		e.setIv(new String(Base64.encode(iv)));
		e.setPassword(encryptedData);

		return e;
	}

	private byte[] generateRandomBytes(int length) {
		byte[] bytes = new byte[length];
		Random r = new Random();
		r.nextBytes(bytes);

		return bytes;
	}
	
	private String findInputValue(String html, String inputName) {
		String[] split = html.split("name=\"" + inputName + "\"");
		String value = split[1].split("/>")[0];
		
		return value.trim().replace("value=\"", "").replace("\"", "");
	}
	
	private BasicClientCookie obtainCookie(String name, String value) {
		BasicClientCookie cookie = new BasicClientCookie(name, value);
	    cookie.setDomain("localhost");
	    cookie.setPath("/saml-identity-provider");
	    
	    return cookie;
	}

	private class NoWrapAutoEndDeflaterOutputStream extends DeflaterOutputStream {

		public NoWrapAutoEndDeflaterOutputStream(final OutputStream os, final int level) {
			super(os, new Deflater(level, true));
		}

		@Override
		public void close() throws IOException {
			if (def != null) {
				def.end();
			}

			super.close();
		}

	}

}
