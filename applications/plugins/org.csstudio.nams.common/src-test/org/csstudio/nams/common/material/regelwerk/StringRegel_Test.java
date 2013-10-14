package org.csstudio.nams.common.material.regelwerk;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class StringRegel_Test extends AbstractTestObject<StringRegel> {

	@Test
	public void testNumeric() throws Throwable {

		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5", null);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "5");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// equal StringRegel - non true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "6");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// equal StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "4");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gt StringRegel - true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "6");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gt StringRegel - non true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "5");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gt StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "4");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gtEqual StringRegel - true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "6");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gtEqual StringRegel - true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "5.001");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gtEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "5");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// gtEqual StringRegel - not true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "4");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// lt StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "6");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// lt StringRegel - not true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "5");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// lt StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "4.999");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// lt StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "4");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// ltEqual StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "6");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// ltEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "5");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// ltEqual StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "4.999");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// ltEqual StringRegel - not true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "5.001");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// ltEqual StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "4");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not Equal StringRegel - true - greater
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "6");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not Equal StringRegel - not true - equal
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "5");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not Equal StringRegel - true - slightly smaller
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "4.999");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not Equal StringRegel - true - slightly greater
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "5.001");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not Equal StringRegel - true - smaller
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "4");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));
	}

	@Test
	public void testText() throws Throwable {
		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.FACILITY, "Some Test-Text", null);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.FACILITY, "Some Test-Text");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.FACILITY, "Some Test-Text", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.FACILITY, "Some Test-Text2");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not equal StringRegel - true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL,
				MessageKeyEnum.HOST, "Some Test-Text", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.HOST, "Some Test-Text2");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// not equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL,
				MessageKeyEnum.HOST, "Some Test-Text", null);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.HOST, "Some Test-Text");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));
	}

	@Test
	public void testTime() throws Throwable {
		// timeAfter StringRegel
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TIME_AFTER,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();

		// ungueltiger Timestamp
		map.put(MessageKeyEnum.LOCATION, "nonsense");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_AFTER_EQUAL,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// timeBefore StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_BEFORE,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// timeBeforeEqual StringRegel
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TIME_BEFORE_EQUAL,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// equal StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_EQUAL,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_NOT_EQUAL,
				MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678", null);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "2008-05-27 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// same day
		map.put(MessageKeyEnum.LOCATION, "2008-05-26 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertFalse(sRegel.pruefeNachricht(new AlarmNachricht(map)));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "2008-05-25 01:23:45.678");
		sRegel.pruefeNachricht(new AlarmNachricht(map));
		Assert.assertTrue(sRegel.pruefeNachricht(new AlarmNachricht(map)));
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
