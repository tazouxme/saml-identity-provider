package com.tazouxme.idp.util;

import org.apache.commons.lang3.RandomStringUtils;

public class IDUtils {
	
	/**
	 * Generate an ID
	 * @param prefix
	 * @param length
	 * @return
	 */
	public static String generateId(String prefix, int length) {
		String r = RandomStringUtils.randomAlphanumeric(length);
		return prefix + r;
	}

}
