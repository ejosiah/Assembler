package com.jay;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CustomerInfo {
	private final String firstname;
	private final String lastname;
	private final int age;
	private final AddressInfo address;
	private final List<PhoneNumberInfo> phoneNumbers;
	private final Long dob;
	private final Map<IDInfo, AddressInfo> addresses;
	private final List<String> initials;
	
	public CustomerInfo(String f, String l, int age, AddressInfo a
			, List<PhoneNumberInfo> phoneNumbers, Long dob
			, Map<IDInfo, AddressInfo> addresses, List<String> initials){
		firstname = f;
		lastname = l;
		this.age = age;
		address = a;
		this.phoneNumbers = phoneNumbers;
		this.dob = dob;
		this.addresses = addresses;
		this.initials = initials;
	}

}
