package org.csstudio.nams.common.material;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

/**
 * CUT: {@link AlarmMessage}.
 */
public class AlarmNachricht_Test extends
		AbstractTestObject<AlarmMessage> {

	@Test
	public void testInitialize() {
		final String message = "Hallo Welt!";

		final AlarmMessage alarmNachricht = new AlarmMessage(message);
		final AlarmMessage gleicheAlarmNachricht = new AlarmMessage(message);
		final AlarmMessage ungleicheAlarmNachricht = new AlarmMessage(
				"Doof!");

		Assert.assertEquals(message, alarmNachricht.gibNachrichtenText());

		Assert.assertEquals(alarmNachricht, gleicheAlarmNachricht);
		Assert.assertFalse(alarmNachricht.equals(ungleicheAlarmNachricht));
	}

	@Test
	public void testLocalClone() {
		final String message = "Hallo Welt!";

		final AlarmMessage alarmNachricht = new AlarmMessage(message);

		final AlarmMessage alarmNachrichtKlon = alarmNachricht.clone();

		Assert.assertFalse(alarmNachricht == alarmNachrichtKlon);
		Assert.assertEquals(alarmNachricht, alarmNachrichtKlon);
	}

	@Override
	protected AlarmMessage getNewInstanceOfClassUnderTest() {
		return new AlarmMessage("Test-Nachricht");
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected AlarmMessage[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new AlarmMessage[] { new AlarmMessage("Test-Nachricht 1"),
				new AlarmMessage("Test-Nachricht 2"),
				new AlarmMessage("Test-Nachricht 3") };
	}
}
