package com.tazouxme.idp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class CookieUtils {
	
	public static Cookie find(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		if (StringUtils.isBlank(key) || cookies == null) {
			return null;
		}
		
		for (Cookie c : cookies) {
			if (c.getName().equals(key)) {
				return c;
			}
		}

		return null;
	}
	
	public static Cookie create(String key, String value, String domain, String path, int expiry) {
		Cookie cookie = new Cookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setMaxAge(expiry);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		return cookie;
	}
	
	public static void add(HttpServletResponse response, String key, String value, String domain, String path, int expiry) {
		Cookie cookie = new Cookie(key, value);
		cookie.setDomain(domain);
		cookie.setPath(path);
		cookie.setMaxAge(expiry);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		
		response.addCookie(cookie);
	}
	
	public static void delete(HttpServletResponse response, Cookie c) {
		if (c == null) {
			return;
		}
		
		c.setMaxAge(0);
		response.addCookie(c);
	}

}
