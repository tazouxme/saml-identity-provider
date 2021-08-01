package com.tazouxme.idp.sanitizer;

public class RegexSanitizerRule {
	
	private String regex;
	private String message;
	
	public RegexSanitizerRule(String regex, String message) {
		this.regex = regex;
		this.message = message;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public String getMessage() {
		return message;
	}

}
