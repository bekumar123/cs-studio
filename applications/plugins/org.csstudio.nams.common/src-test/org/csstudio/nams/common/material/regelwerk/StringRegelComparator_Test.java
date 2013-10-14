package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class StringRegelComparator_Test extends AbstractTestObject<StringRegel> {

	@Test
	public void testNumeric() throws Throwable {

		// equal StringRegel - true
		StringRegelComparator sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				true);
		Assert.assertTrue(sComparator.compare("5", "5"));

		// equal StringRegel - non true - greater
		Assert.assertFalse(sComparator.compare("5", "6"));

		// equal StringRegel - non true - smaller
		Assert.assertFalse(sComparator.compare("5", "4"));

		// gt StringRegel - true
		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_NUMERIC_GT,
				true);
		Assert.assertTrue(sComparator.compare("6", "5"));

		// gt StringRegel - non true - equal
		Assert.assertFalse(sComparator.compare("5", "5"));

		// gt StringRegel - non true - smaller
		Assert.assertFalse(sComparator.compare("4", "5"));

		// gtEqual StringRegel - true - greater
		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				true);
		Assert.assertTrue(sComparator.compare("6", "5"));

		// gtEqual StringRegel - true - slightly greater
		Assert.assertTrue(sComparator.compare("5.001", "5"));

		// gtEqual StringRegel - true - equal
		Assert.assertTrue(sComparator.compare("5", "5"));

		// gtEqual StringRegel - not true - smaller
		Assert.assertFalse(sComparator.compare("4", "5"));

		// gtEqual StringRegel - not true - smaller
		Assert.assertFalse(sComparator.compare("4.999", "5"));
		
		// lt StringRegel - not true - greater
		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_NUMERIC_LT,
				true);
		Assert.assertFalse(sComparator.compare("6", "5"));

		// lt StringRegel - not true - equal
		Assert.assertFalse(sComparator.compare("5", "5"));

		// lt StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999", "5"));

		// lt StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4", "5"));


		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				true);
		// ltEqual StringRegel - not true - greater
		Assert.assertFalse(sComparator.compare("6", "5"));
		// ltEqual StringRegel - not true - slightly greater
		Assert.assertFalse(sComparator.compare("5.001", "5"));
		// ltEqual StringRegel - true - equal
		Assert.assertTrue(sComparator.compare("5", "5"));
		// ltEqual StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999", "5"));
		// ltEqual StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4", "5"));

		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				true);
		// not Equal StringRegel - true - greater
		Assert.assertTrue(sComparator.compare("6", "5"));
		// not Equal StringRegel - true - slightly greater
		Assert.assertTrue(sComparator.compare("5.001", "5"));
		// not Equal StringRegel - not true - equal
		Assert.assertFalse(sComparator.compare("5", "5"));
		// not Equal StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999", "5"));
		// not Equal StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4", "5"));
		
	}

	@Test
	public void testText() throws Throwable {
		String text = "Some Test-Text";

		// equal StringRegel - true
		StringRegelComparator sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TEXT_EQUAL, true);
		Assert.assertTrue(sComparator.compare(text, "Some Test-Text"));

		// equal StringRegel - not true
		Assert.assertFalse(sComparator.compare(text, "Some Test-Text2"));

		// not equal StringRegel - true
		sComparator = new StringRegelComparator(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL,
				true);
		Assert.assertTrue(sComparator.compare(text, "Some Test-Text2"));

		// not equal StringRegel - not true
		Assert.assertFalse(sComparator.compare(text, "Some Test-Text"));
	}

	@Test
	public void testTime() throws Throwable {
		String dateString = "2008-05-26 01:23:45.678";
		// timeAfter StringRegel
		StringRegelComparator sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_AFTER,true);

		// ungueltiger Timestamp
		try {
			sComparator.compare(dateString, "nonsense");
			Assert.fail();
		} catch(Exception e) {
		}

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678", dateString));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678", dateString));

		// timeAfterEqual StringRegel
		sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_AFTER_EQUAL,true);

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678", dateString));
		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678", dateString));

		// timeBefore StringRegel
		sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_BEFORE,true);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678", dateString));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678", dateString));

		// timeBeforeEqual StringRegel
		sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_BEFORE_EQUAL,true);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678", dateString));

		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678", dateString));

		// equal StringRegel
		sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_EQUAL,true);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678", dateString));

		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678", dateString));

		// timeAfterEqual StringRegel
		sComparator = new StringRegelComparator(
				StringRegelOperator.OPERATOR_TIME_NOT_EQUAL,true);

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678", dateString));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678", dateString));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678", dateString));
	}

	@Override
	protected StringRegel getNewInstanceOfClassUnderTest() {
		return new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5", null);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StringRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final StringRegel[] regels = new StringRegel[3];
		regels[0] = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.APPLICATION_ID, "5", null);
		regels[1] = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "6", null);
		regels[2] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.DESTINATION, "7", null);

		return regels;
	}

}
