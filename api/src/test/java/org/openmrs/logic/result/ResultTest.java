/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.logic.result;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;

/**
 * Tests all methods on the {@link Result} object
 * 
 * @see Result
 */
public class ResultTest {
	
	@Test
	public void toObject_shouldReturnResultObjectForSingleResults() {
		Result firstResult = new Result(new Date(), "some value", new Encounter(123));
		
		assertEquals(123, ((Encounter) firstResult.toObject()).getId().intValue());
	}
	
	@Test
	public void Result_shouldNotFailWithNullList() {
		new Result((List<Result>) null);
	}
	
	@Test
	public void Result_shouldNotFailWithEmptyList() {
		new Result(new ArrayList<Result>());
	}
	
	@Test
	public void Result_shouldNotFailWithNullResult() {
		new Result((Result) null);
	}
	
	/*
	 * @see Result#Result(Obs)
	 */
	@Test
	public void Result_shouldSetDatatypeBasedOnObs() {
		Obs obs = new Obs();
		Concept concept = new Concept();
		ConceptDatatype conceptDatatype = new ConceptDatatype();

		conceptDatatype.setUuid(ConceptDatatype.BOOLEAN_UUID); // boolean
		concept.setDatatype(conceptDatatype);
		obs.setConcept(concept);

		Result result = new Result(obs);
		assertEquals(Result.Datatype.BOOLEAN, result.getDatatype());
	}

	/*
	 * @see Result#Result(Integer)
	 */
	@Test
	public void Result_shouldSetIntegerValueAndDefaultDate() {
		Integer valueNumeric = 123;
		Result result = new Result(valueNumeric);

		assertEquals(valueNumeric.doubleValue(), result.toNumber());
		assertNull(result.toDatetime());
	}

	/*
	 * @see Result#Result(Boolean)
	 */
	@Test
	public void Result_shouldSetBooleanValueAndDefaultDate() {
		Boolean valueBoolean = true;
		Result result = new Result(valueBoolean);
		
		assertEquals(valueBoolean, result.toBoolean());
		assertNull(result.toDatetime());
	}
	

	/*
	 * @see Result#Result(Date, Datatype, Boolean, Concept, Date, Double, String, Object)
	 */
	@Test
	public void Result_shouldSetAllValues() {
		Date resultDate = new Date();
		Boolean valueBoolean = true;
		Concept valueCoded = new Concept();
		Date valueDatetime = new Date();
		Double valueNumeric = 123.45;
		String valueText = "test";
		Object object = new Object();

		Result result = new Result(resultDate, Result.Datatype.NUMERIC, valueBoolean, valueCoded, valueDatetime, valueNumeric, valueText, object);

		assertEquals(resultDate, result.getResultDate());
		assertEquals(valueBoolean, result.toBoolean());
		assertEquals(valueCoded, result.toConcept());
		assertEquals(valueDatetime, result.toDatetime());
		assertEquals(valueNumeric, result.toNumber());
		assertEquals(String.valueOf(valueNumeric), result.toString());
		assertEquals(object, result.toObject());
	}
	
	@Test
	public void earliest_shouldGetTheFirstResultGivenMultipleResults() throws ParseException {
		Result parentResult = new Result();
		Result secondResult = new Result(Context.getDateFormat().parse("15/08/2008"), "some other value", new Encounter(124));
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some value", new Encounter(123));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some value", parentResult.earliest().toString());
	}
	
	@Test
	public void earliest_shouldGetTheResultGivenASingleResult() throws ParseException {
		Result parentResult = new Result();
		Result secondResult = new Result(Context.getDateFormat().parse("15/08/2008"), "some other value", new Encounter(124));
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some value", new Encounter(123));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some value", parentResult.earliest().toString());
	}
	
	@Test
	public void earliest_shouldGetAnEmptyResultGivenAnEmptyResult() {
		Result parentResult = new EmptyResult();
		assertEquals(new EmptyResult(), parentResult.earliest());
	}
	
	@Test
	public void earliest_shouldNotGetTheResultWithNullResultDateGivenOtherResults() throws ParseException {
		Result parentResult = new Result();
		Result secondResult = new Result(null, "some value", new Encounter(123));
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some other value", parentResult.earliest().toString());
	}
	
	@Test
	public void earliest_shouldGetOneResultWithNullResultDatesForAllResults() {
		Result parentResult = new Result();
		Result firstResult = new Result(null, "some value", new Encounter(123));
		Result secondResult = new Result(null, "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some value", parentResult.earliest().toString());
	}
	
	@Test
	public void equals_shouldReturnTrueOnTwoEmptyResults() {
		assertTrue(new EmptyResult().equals(new Result()));
	}

	@Test
	public void equals_shouldReturnTrueOnEqualResults() {
		Result result1 = new Result(new Date(), "some value", new Encounter(123));
		Result result2 = new Result(new Date(), "some value", new Encounter(123));

		assertTrue(result1.equals(result2));
	}

	@Test
	public void equals_shouldReturnFalseOnDifferentResults() {
		Result result1 = new Result(new Date(), "value1", new Encounter(123));
		Result result2 = new Result(new Date(), "value2", new Encounter(456));

		assertFalse(result1.equals(result2));
	}
	
	@Test
	public void get_shouldGetEmptyResultForIndexesOutOfRange() throws ParseException {
		Result parentResult = new Result();
		Result secondResult = new Result(null, "some value", new Encounter(123));
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		// 3 is greater than the number of entries in the parentResult
		assertEquals(new EmptyResult(), parentResult.get(3));
	}
	
	@Test
	public void isNull_shouldReturnFalse() {
		assertFalse(new Result().isNull());
	}
	
	@Test
	public void latest_shouldGetTheMostRecentResultGivenMultipleResults() throws ParseException {
		Result parentResult = new Result();
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some other value", new Encounter(124));
		Result secondResult = new Result(Context.getDateFormat().parse("15/08/2008"), "some value", new Encounter(123));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some value", parentResult.latest().toString());
	}
	
	@Test
	public void latest_shouldGetTheResultGivenASingleResult() throws ParseException {
		Result result = new Result(Context.getDateFormat().parse("12/08/2008"), "some other value", new Encounter(124));
		
		assertEquals("some other value", result.latest().toString());
	}
	
	@Test
	public void latest_shouldGetAnEmptyResultGivenAnEmptyResult() {
		assertEquals(new EmptyResult(), new Result().latest());
	}
	
	@Test
	public void latest_shouldGetTheResultWithNullResultDate() throws ParseException {
		Result parentResult = new Result();
		Result firstResult = new Result(Context.getDateFormat().parse("15/08/2008"), "some value", new Encounter(123));
		Result secondResult = new Result(null, "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertEquals("some value", parentResult.latest().toString());
	}
	
	@Test
	public void toObject_shouldFailWhenContainsMultipleResults() throws ParseException {
		Result parentResult = new Result();
		Result firstResult = new Result(Context.getDateFormat().parse("12/08/2008"), "some value", new Encounter(123));
		Result secondResult = new Result(Context.getDateFormat().parse("15/08/2008"), "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		assertThrows(LogicException.class, () -> parentResult.toObject());
	}
}
