package com.tazouxme.saml.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class ClientRegisterTest extends AbstractClientTest {

	private static final String REGISTER_URL = "http://localhost:20126/saml-identity-provider/register";

	@Test
	@Order(1)
	public void testRegisterEmptyValue() {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpGet get = new HttpGet(REGISTER_URL);
			CloseableHttpResponse registerPage = httpClient.execute(get);
			assertEquals(200, registerPage.getStatusLine().getStatusCode());

			String text = new BufferedReader(
					new InputStreamReader(registerPage.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Register to SSO"));

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", ""));
			params.add(new BasicNameValuePair("domain", "xx.com"));
			params.add(new BasicNameValuePair("username", "helloworld"));
			params.add(new BasicNameValuePair("password", "pass1234"));

			HttpPost post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Value cannot be empty"));
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", "my_org"));
			params.add(new BasicNameValuePair("domain", ""));
			params.add(new BasicNameValuePair("username", "helloworld"));
			params.add(new BasicNameValuePair("password", "pass1234"));

			post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Value cannot be empty"));
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", "my_org"));
			params.add(new BasicNameValuePair("domain", "xx.com"));
			params.add(new BasicNameValuePair("username", ""));
			params.add(new BasicNameValuePair("password", "pass1234"));

			post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Value cannot be empty"));
		} catch (Exception e) {
			fail(e);
		}
	}

	@Test
	@Order(2)
	public void testRegisterWrongDomain() {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpGet get = new HttpGet(REGISTER_URL);
			CloseableHttpResponse registerPage = httpClient.execute(get);
			assertEquals(200, registerPage.getStatusLine().getStatusCode());

			String text = new BufferedReader(
					new InputStreamReader(registerPage.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Register to SSO"));

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", "my_org"));
			params.add(new BasicNameValuePair("domain", "xx"));
			params.add(new BasicNameValuePair("username", "helloworld"));
			params.add(new BasicNameValuePair("password", "pass1234"));

			HttpPost post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Domain not correctly formed"));
		} catch (Exception e) {
			fail(e);
		}
	}

	@Test
	@Order(3)
	public void testRegisterWrongPassword() {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpGet get = new HttpGet(REGISTER_URL);
			CloseableHttpResponse registerPage = httpClient.execute(get);
			assertEquals(200, registerPage.getStatusLine().getStatusCode());

			String text = new BufferedReader(
					new InputStreamReader(registerPage.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Register to SSO"));

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", "my_org"));
			params.add(new BasicNameValuePair("domain", "xx.com"));
			params.add(new BasicNameValuePair("username", "helloworld"));
			params.add(new BasicNameValuePair("password", "pass"));

			HttpPost post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Password length must be min. 8 and must contain 1 letter and 1 number"));
		} catch (Exception e) {
			fail(e);
		}
	}

	@Test
	@Order(4)
	public void testRegister() {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpGet get = new HttpGet(REGISTER_URL);
			CloseableHttpResponse registerPage = httpClient.execute(get);
			assertEquals(200, registerPage.getStatusLine().getStatusCode());

			String text = new BufferedReader(
					new InputStreamReader(registerPage.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
							.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Register to SSO"));

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("organization", "my_org"));
			params.add(new BasicNameValuePair("domain", "xx.com"));
			params.add(new BasicNameValuePair("username", "helloworld"));
			params.add(new BasicNameValuePair("password", "pass1234"));

			HttpPost post = new HttpPost(REGISTER_URL);
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse registeredUser = httpClient.execute(post);
			assertEquals(200, registeredUser.getStatusLine().getStatusCode());
			
			text = new BufferedReader(
					new InputStreamReader(registeredUser.getEntity().getContent(), StandardCharsets.UTF_8)).lines()
					.collect(Collectors.joining("\n"));

			assertTrue(text.contains("Validate your instance"));
		} catch (Exception e) {
			fail(e);
		}
	}

}
