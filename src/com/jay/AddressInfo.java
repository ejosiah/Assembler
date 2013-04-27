package com.jay;

public class AddressInfo {
	private final String street;
	private final String city;
	private final String postcode;
	
	public AddressInfo(String s, String c, String p){
		street = s;
		city = c;
		postcode = p;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getPostcode() {
		return postcode;
	}
}
