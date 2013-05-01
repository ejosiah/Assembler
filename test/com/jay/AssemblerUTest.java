package com.jay;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class AssemblerUTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		Calendar dob = Calendar.getInstance();
		dob.set(Calendar.YEAR, 1983);
		dob.set(Calendar.MONTH, Calendar.SEPTEMBER);
		dob.set(Calendar.DATE, 1);
		
		AddressInfo add = new AddressInfo("17 Mead Road", "Edgware", "HA8 6LH");
		AddressInfo add2 = new AddressInfo("15 Mead Road", "Edgware", "HA8 6LH");
		IDInfo id = new IDInfo(UUID.randomUUID().toString().toUpperCase());
		IDInfo id2 = new IDInfo(UUID.randomUUID().toString().toUpperCase());
		
		Map<IDInfo, AddressInfo> map = new HashMap<IDInfo, AddressInfo>();
		map.put(id, add);
		map.put(id2, add2);
		
		List<String> initials = Arrays.asList("J.J", "JOJ", "S.H");
		
		CustomerInfo cust = new CustomerInfo("Josiah", "Ebhomenye", 29, add
				, new ArrayList<PhoneNumberInfo>(Arrays.asList(
				new PhoneNumberInfo("07854200230"), 
				new PhoneNumberInfo("0792355555"), 
				new PhoneNumberInfo("20708540215"))), dob.getTimeInMillis(), map, initials);
		
		Address address = new Address("17 Mead Road", "Edgware", "HA8 6LH");
		Address address2 = new Address("15 Mead Road", "Edgware", "HA8 6LH");
		
		Map<ID, Address> map2 = new HashMap<ID, Address>();
		map2.put(new ID(id.getValue()), address);
		map2.put(new ID(id2.getValue()), address2);
		
		Customer expected = new Customer("Josiah", "Ebhomenye", 29, address, Arrays.asList(
				new PhoneNumber("07854200230"), 
				new PhoneNumber("0792355555"), 
				new PhoneNumber("20708540215")), dob.getTime(), map2, initials);
		
		Customer actual = new Assembler().assemble(cust, Customer.class);
		actual = new Assembler().assemble(cust, Customer.class);
		assertEquals(expected, actual);
		System.out.println(actual);
	}
	
	@Test(expected=AssemblyException.class)
	public void testNoXmlFile(){
		Calendar dob = Calendar.getInstance();
		dob.set(Calendar.YEAR, 1983);
		dob.set(Calendar.MONTH, Calendar.SEPTEMBER);
		dob.set(Calendar.DATE, 1);
		
		AddressInfo add = new AddressInfo("17 Mead Road", "Edgware", "HA8 6LH");
		AddressInfo add2 = new AddressInfo("15 Mead Road", "Edgware", "HA8 6LH");
		IDInfo id = new IDInfo(UUID.randomUUID().toString().toUpperCase());
		IDInfo id2 = new IDInfo(UUID.randomUUID().toString().toUpperCase());
		
		Map<IDInfo, AddressInfo> map = new HashMap<IDInfo, AddressInfo>();
		map.put(id, add);
		map.put(id2, add2);
		
		List<String> initials = Arrays.asList("J.J", "JOJ", "S.H");
		
		CustomerInfo cust = new CustomerInfo("Josiah", "Ebhomenye", 29, add, Arrays.asList(
				new PhoneNumberInfo("07854200230"), 
				new PhoneNumberInfo("0792355555"), 
				new PhoneNumberInfo("20708540215")), dob.getTimeInMillis(), map, initials);
		
		new Assembler().assemble(cust, FalseCustomer.class);
	}

}
