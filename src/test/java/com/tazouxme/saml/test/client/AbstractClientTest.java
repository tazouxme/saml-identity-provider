package com.tazouxme.saml.test.client;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.tazouxme.saml.test.util.DbKiller;

@ContextConfiguration("classpath:spring/db-killer.xml")
@DirtiesContext
@ExtendWith(SpringExtension.class)
@Rollback(value = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public abstract class AbstractClientTest {

	private static Server server;
	
	@Autowired
	private DbKiller killer;

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
	}

	@AfterAll
	public void stop() throws Exception {
		killer.killAll();
		server.stop();
		server.destroy();
	}

}
