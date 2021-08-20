package com.tazouxme.idp.sanitizer.entity;

public class Equality<T> {
	
	private T v1;
	private T v2;
	
	public Equality(T v1, T v2) {
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public boolean areEquals() {
		if (v1 == null) {
			return v2 == null;
		}
		
		return v1.equals(v2);
	}

}
