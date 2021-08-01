package com.tazouxme.idp.util;

import org.apache.commons.lang3.RandomStringUtils;

public class IDUtils {
	
	public static String generateId(String prefix, int length) {
		String r = RandomStringUtils.randomAlphanumeric(length);
		return prefix + r;
	}

}
