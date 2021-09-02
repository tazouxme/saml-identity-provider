package com.tazouxme.idp.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
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
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensaml.core.config.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.idp.IdentityProviderConstants;
import com.tazouxme.idp.security.filter.entity.PasswordEntity;
import com.tazouxme.idp.test.util.DbKiller;

@ContextConfiguration("classpath:spring/db-killer.xml")
@DirtiesContext
@ExtendWith(SpringExtension.class)
@Rollback(value = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public abstract class AbstractServiceTest {

	private static final String DASHBOARD_URL = "http://localhost:20126/saml-identity-provider/dashboard";
	private static final String LOGIN_URL = "http://localhost:20126/saml-identity-provider/login";
	
	protected static final String API_URL = "http://localhost:20126/saml-identity-provider/services/api/v1";

	private static Server server;
	
	protected CloseableHttpClient httpClient = HttpClients.createDefault();
	
	@Autowired
	private DbKiller killer;
	
	@Autowired
	protected ResourceLoader resourceLoader;

	@BeforeAll
	public void start() throws Exception {
		InitializationService.initialize();
		Security.addProvider(new BouncyCastleProvider());

		WebAppContext context = new WebAppContext();
		context.setDescriptor("src/test/resources/web.xml");
		context.setResourceBase("src/main/webapp");
		context.setContextPath("/saml-identity-provider");
		context.setParentLoaderPriority(true);

		server = new Server(20126);
		server.setHandler(context);
		server.start();
		
		connect();
	}

	@AfterAll
	public void stop() throws Exception {
		killer.killAll();
		server.stop();
		server.destroy();
	}
	
	protected String get(int expectedCode, String uri) throws IOException {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ClientHttpRequest get = factory.createRequest(URI.create(API_URL + uri), HttpMethod.GET);
		
		try {
			ClientHttpResponse response = get.execute();
			assertEquals(expectedCode, response.getStatusCode().value());

			return extractInputStream(response.getBody());
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected String post(int expectedCode, String uri, String entity) throws IOException {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ClientHttpRequest post = factory.createRequest(URI.create(API_URL + uri), HttpMethod.POST);
		post.getHeaders().add("content-type", "application/json");
		
		if (!StringUtils.isBlank(entity)) {
			post.getBody().write(entity.getBytes());
		}
		
		try {
			ClientHttpResponse response = post.execute();
			assertEquals(expectedCode, response.getStatusCode().value());

			return extractInputStream(response.getBody());
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected String put(int expectedCode, String uri, String entity) throws IOException {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ClientHttpRequest put = factory.createRequest(URI.create(API_URL + uri), HttpMethod.PUT);
		put.getHeaders().add("content-type", "application/json");
		
		if (!StringUtils.isBlank(entity)) {
			put.getBody().write(entity.getBytes());
		}
		
		try {
			ClientHttpResponse response = put.execute();
			assertEquals(expectedCode, response.getStatusCode().value());

			return extractInputStream(response.getBody());
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected String patch(int expectedCode, String uri, String entity) throws IOException {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ClientHttpRequest patch = factory.createRequest(URI.create(API_URL + uri), HttpMethod.PATCH);
		patch.getHeaders().add("content-type", "application/json");
		
		if (!StringUtils.isBlank(entity)) {
			patch.getBody().write(entity.getBytes());
		}
		
		try {
			ClientHttpResponse response = patch.execute();
			assertEquals(expectedCode, response.getStatusCode().value());

			return extractInputStream(response.getBody());
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected String delete(int expectedCode, String uri, String entity) throws IOException {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		ClientHttpRequest delete = factory.createRequest(URI.create(API_URL + uri), HttpMethod.DELETE);
		delete.getHeaders().add("content-type", "application/json");
		
		if (!StringUtils.isBlank(entity)) {
			delete.getBody().write(entity.getBytes());
		}
		
		try {
			ClientHttpResponse response = delete.execute();
			assertEquals(expectedCode, response.getStatusCode().value());

			return extractInputStream(response.getBody());
		} catch (IOException e) {
			throw e;
		}
	}
	
	protected String extractInputStream(InputStream is) throws UnsupportedOperationException, IOException {
		String responseText = new BufferedReader(
				new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		
		System.out.println(responseText);
		return responseText;
	}
	
	private void connect() {
		try {
			HttpGet get = new HttpGet(DASHBOARD_URL);
			
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
			params.add(new BasicNameValuePair("password", "{\"password\":" + "\"" + password.getPassword() + "\","
					+ "\"iv\":" + "\"" + password.getIv() + "\"}"));

			HttpPost post = new HttpPost(LOGIN_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse loggedinUser = httpClient.execute(post); // -- User is now logged in
			assertEquals(302, loggedinUser.getStatusLine().getStatusCode());
		} catch (Exception e) {
			fail(e);
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

}
