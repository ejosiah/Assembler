package com.jay;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
public class Customer {
	private final String firstname;
	private final String lastname;
	private final Integer age;
	private final Address address;
	private final List<PhoneNumber> phoneNumbers;
	private Date dob;
	private Map<ID, Address> addresses;
	private final List<String> initials;
	
	public Customer(String f, String l, Integer ag, Address a
			, List<PhoneNumber> phoneNumbers, Date dob
			, Map<ID, Address> addresses, List<String> initials){
		firstname = f;
		lastname = l;
		age = ag;
		address = a;
		this.phoneNumbers = phoneNumbers;
		this.dob = dob;
		this.addresses = addresses;
		this.initials = initials;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public int getAge() {
		return age;
	}

	public Address getAddress() {
		return address;
	}
}	
