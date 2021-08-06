package com.tazouxme.idp.messages;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;

import com.tazouxme.idp.IdentityProviderConstants;

public class Messages {
	
	private static final String BUNDLE_NAME = "i18n/messages";

	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}
	
	public static Messages find(HttpSession session) {
		Messages m = (Messages) session.getAttribute(IdentityProviderConstants.SESSION_ATTRIBUTE_MESSAGES);
		
		if (m == null) {
			m = new Messages();
			session.setAttribute(IdentityProviderConstants.SESSION_ATTRIBUTE_MESSAGES, m);
		}
		
		return m;
	}

	public String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public String getString(String key, Object... args) {
		try {
			return new MessageFormat(getString(key)).format(args);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public void setResourceBundle(Locale locale) {
		RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, locale);
	}

}
