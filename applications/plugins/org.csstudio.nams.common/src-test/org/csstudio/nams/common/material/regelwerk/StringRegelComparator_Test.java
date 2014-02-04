package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class StringRegelComparator_Test extends AbstractTestObject<StringFilterCondition> {

	@Test
	public void testNumeric() throws Throwable {

		// equal StringRegel - true
		StringFilterConditionComparator sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_NUMERIC_EQUAL,
				true);
		sComparator.setComparedString("5");
		Assert.assertTrue(sComparator.compare("5"));

		// equal StringRegel - non true - greater
		sComparator.setComparedString("6");
		Assert.assertFalse(sComparator.compare("5"));

		// equal StringRegel - non true - smaller
		sComparator.setComparedString("4");
		Assert.assertFalse(sComparator.compare("5"));

		// gt StringRegel - true
		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_NUMERIC_GT,
				true);
		sComparator.setComparedString("5");
		Assert.assertTrue(sComparator.compare("6"));

		// gt StringRegel - non true - equal
		Assert.assertFalse(sComparator.compare("5"));

		// gt StringRegel - non true - smaller
		Assert.assertFalse(sComparator.compare("4"));

		// gtEqual StringRegel - true - greater
		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_NUMERIC_GT_EQUAL,
				true);
		sComparator.setComparedString("5");
		Assert.assertTrue(sComparator.compare("6"));

		// gtEqual StringRegel - true - slightly greater
		Assert.assertTrue(sComparator.compare("5.001"));

		// gtEqual StringRegel - true - equal
		Assert.assertTrue(sComparator.compare("5"));

		// gtEqual StringRegel - not true - smaller
		Assert.assertFalse(sComparator.compare("4"));

		// gtEqual StringRegel - not true - smaller
		Assert.assertFalse(sComparator.compare("4.999"));
		
		// lt StringRegel - not true - greater
		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_NUMERIC_LT,
				true);
		sComparator.setComparedString("5");
		
		Assert.assertFalse(sComparator.compare("6"));

		// lt StringRegel - not true - equal
		Assert.assertFalse(sComparator.compare("5"));

		// lt StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999"));

		// lt StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4"));


		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_NUMERIC_LT_EQUAL,
				true);
		sComparator.setComparedString("5");
		// ltEqual StringRegel - not true - greater
		Assert.assertFalse(sComparator.compare("6"));
		// ltEqual StringRegel - not true - slightly greater
		Assert.assertFalse(sComparator.compare("5.001"));
		// ltEqual StringRegel - true - equal
		Assert.assertTrue(sComparator.compare("5"));
		// ltEqual StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999"));
		// ltEqual StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4"));

		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				true);
		sComparator.setComparedString("5");
		// not Equal StringRegel - true - greater
		Assert.assertTrue(sComparator.compare("6"));
		// not Equal StringRegel - true - slightly greater
		Assert.assertTrue(sComparator.compare("5.001"));
		// not Equal StringRegel - not true - equal
		Assert.assertFalse(sComparator.compare("5"));
		// not Equal StringRegel - true - slightly smaller
		Assert.assertTrue(sComparator.compare("4.999"));
		// not Equal StringRegel - true - smaller
		Assert.assertTrue(sComparator.compare("4"));
		
	}

	@Test
	public void testText() throws Throwable {
		// equal StringRegel - true
		StringFilterConditionComparator sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TEXT_EQUAL, true);
		sComparator.setComparedString("Some Test-Text");
		
		Assert.assertTrue(sComparator.compare("Some Test-Text"));

		// equal StringRegel - not true
		Assert.assertFalse(sComparator.compare("Some Test-Text2"));

		// not equal StringRegel - true
		sComparator = new StringFilterConditionComparator(StringFilterConditionOperator.OPERATOR_TEXT_NOT_EQUAL,
				true);
		sComparator.setComparedString("Some Test-Text");

		Assert.assertTrue(sComparator.compare("Some Test-Text2"));

		// not equal StringRegel - not true
		Assert.assertFalse(sComparator.compare("Some Test-Text"));
	}

	@Test
	public void testTime() throws Throwable {
		String dateString = "2008-05-26 01:23:45.678";
		// timeAfter StringRegel
		StringFilterConditionComparator sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_AFTER,true);
		sComparator.setComparedString(dateString);

		// ungueltiger Timestamp
		try {
			sComparator.compare("nonsense");
			Assert.fail();
		} catch(Exception e) {
		}

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678"));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678"));

		// timeAfterEqual StringRegel
		sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_AFTER_EQUAL,true);
		sComparator.setComparedString(dateString);

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678"));
		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678"));

		// timeBefore StringRegel
		sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_BEFORE,true);
		sComparator.setComparedString(dateString);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678"));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678"));

		// timeBeforeEqual StringRegel
		sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_BEFORE_EQUAL,true);
		sComparator.setComparedString(dateString);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678"));

		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678"));

		// equal StringRegel
		sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_EQUAL,true);
		sComparator.setComparedString(dateString);

		// one day later
		Assert.assertFalse(sComparator.compare("2008-05-27 01:23:45.678"));

		// same day
		Assert.assertTrue(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertFalse(sComparator.compare("2008-05-25 01:23:45.678"));

		// timeAfterEqual StringRegel
		sComparator = new StringFilterConditionComparator(
				StringFilterConditionOperator.OPERATOR_TIME_NOT_EQUAL,true);
		sComparator.setComparedString(dateString);

		// one day later
		Assert.assertTrue(sComparator.compare("2008-05-27 01:23:45.678"));

		// same day
		Assert.assertFalse(sComparator.compare("2008-05-26 01:23:45.678"));

		// one day before
		Assert.assertTrue(sComparator.compare("2008-05-25 01:23:45.678"));
	}

	@Override
	protected StringFilterCondition getNewInstanceOfClassUnderTest() {
		return new StringFilterCondition(StringFilterConditionOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5", null);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StringFilterCondition[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final StringFilterCondition[] regels = new StringFilterCondition[3];
		regels[0] = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.APPLICATION_ID, "5", null);
		regels[1] = new StringFilterCondition(
				StringFilterConditionOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "6", null);
		regels[2] = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.DESTINATION, "7", null);

		return regels;
	}

}
