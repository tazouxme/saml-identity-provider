package com.tazouxme.idp.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tazouxme.idp.mail.exception.IdentityProviderMailException;
import com.tazouxme.idp.model.User;

public abstract class IdentityProviderMail {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private final String username;
	private final String password;
	
	public IdentityProviderMail(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public boolean doSend(String link, User user) throws IdentityProviderMailException {
		if (!isPreCheckFine(link, user)) {
			logger.error("Mail sender preCheck failed");
			return false;
		}
		
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(prop, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}

		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("joel.tazzari@gmail.com"));
//			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
			message.setSubject("Account activation");
			message.setText("<a href=\"" + link + "\">Validate your instance</a>");

			Transport.send(message);
			
			logger.info("Link for activation generated and sent by mail");
			postSend();
			return true;
		} catch (MessagingException e) {
			throw new IdentityProviderMailException("Error during mail sending", e);
		}
	}
	
	protected abstract boolean isPreCheckFine(String link, User user) throws IdentityProviderMailException;
	
	protected abstract void postSend() throws IdentityProviderMailException;

}
