/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * This tests methods on the provider object.
 */
public class ProviderTest {
	
	/**
	 * @see Provider#getName()
	 */
	@Test
	public void getName_shouldReturnPersonFullNameIfPersonIsNotNullOrNullOtherwise() {
		Provider provider = new Provider();
		
		Person person = new Person(1);
		person.addName(new PersonName("givenName", "middleName", "familyName"));
		provider.setPerson(person);
		assertEquals(person.getPersonName().getFullName(), provider.getName());
	}
	
	/**
	 * @see Provider#getName()
	 */
	@Test
	public void getName_shouldReturnNullIfPersonNameIsNull() {
		Provider provider = new Provider();
		Person person = new Person(1);
		provider.setPerson(person);
		assertEquals(null, provider.getName());
	}
	
	/**
	 * @see Provider#getName()
	 */
	@Test
	public void getName_shouldReturnNullIfPersonIsNull() {
		Provider provider = new Provider();
		assertEquals(null, provider.getName());
	}
	
	/**
	 * @see Provider#toString()
	 */
	@Test
	public void toString_shouldReturnPersonAllNamesWithSpecificFormat() {
		
		Provider provider = new Provider();
		provider.setProviderId(1);
		
		Person person = new Person(1);
		person.addName(new PersonName("givenName", "middleName", "familyName"));
		provider.setPerson(person);
		assertEquals(provider.toString(), "[Provider: providerId:1 providerName:[givenName middleName familyName] ]");
	}
	
	/**
	 * @see Provider#toString()
	 */
	@Test
	public void toString_shouldReturnSpecificFormatIfPersonIsNull() {
		Provider provider = new Provider();
		provider.setProviderId(1);
		provider.setPerson(null);
		assertEquals(provider.toString(), "[Provider: providerId:1 providerName: ]");
	}
	
	/**
	 * @see Provider#toString()
	 */
	@Test
	public void toString_shouldReturnSpecificFormatIfPersonHasSingleName() {
		Provider provider = new Provider();
		provider.setProviderId(1);

		Person person = new Person(1);
		person.addName(new PersonName("givenName", null, null));
		provider.setPerson(person);
		assertEquals(provider.toString(), "[Provider: providerId:1 providerName:[givenName] ]");
	}
	
	/**
	 * @see Provider#toString()
	 */
	@Test
	public void toString_shouldReturnSpecificFormatIfPersonNamesIsEmpty() {
		Provider provider = new Provider();
		provider.setProviderId(1);
		Person person = new Person(1);
		person.addName(new PersonName());
		provider.setPerson(person);
		assertEquals(provider.toString(), "[Provider: providerId:1 providerName:[] ]");
	}
}
