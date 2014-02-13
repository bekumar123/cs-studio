package org.csstudio.nams.common.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class Vorgangsmappe_Test extends AbstractTestObject<MessageCasefile> {

	@Test
	public void testAbgeschlossenDurch() throws UnknownHostException {
		final CasefileId kennung = CasefileId.createNew();
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile mappe = new MessageCasefile(kennung, alarmNachricht);
		Assert
				.assertFalse("mappe.istAbgeschlossen()", mappe
						.isClosed());
		final CasefileId abschliesserKennung = CasefileId.createNew();
		mappe.closeWithFileId(abschliesserKennung);
		Assert.assertTrue("mappe.istAbgeschlossen()", mappe.isClosed());
		Assert.assertTrue(
						"abschliesserKennung.equals(mappe.gibAbschliessendeMappenkennung())",
						abschliesserKennung.equals(mappe
								.getClosedByFileId()));
	}

	@Test
	public void testGibAusloesendeAlarmNachrichtDiesesVorganges()
			throws UnknownHostException {
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final CasefileId vorgangsmappenkennung = CasefileId.createNew();
		final MessageCasefile vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		Assert.assertSame(
				"Hineingereichte Nachricht ist auch die, die herauskommt",
				alarmNachricht, vorgangsmappe
						.getAlarmMessage());
	}

	@Test
	public void testKopieren() throws UnknownHostException {
		final CasefileId kennung = CasefileId.createNew();
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");

		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		final MessageCasefile neueVorgangsmappe = vorgangsmappe
				.getCopyFor("Horst Senkel");

		Assert.assertNotNull(neueVorgangsmappe);
		Assert.assertFalse(neueVorgangsmappe == vorgangsmappe);
		Assert.assertNotNull(neueVorgangsmappe
				.getAlarmMessage());
		Assert.assertFalse(neueVorgangsmappe
				.getAlarmMessage() == vorgangsmappe
				.getAlarmMessage());
		Assert
				.assertEquals(vorgangsmappe
						.getAlarmMessage(),
						neueVorgangsmappe
								.getAlarmMessage());
		Assert.assertFalse("Kennung bleibt nicht gleich!",
				kennung == neueVorgangsmappe.getCasefileId());
		Assert.assertFalse("Kennung bleibt nicht gleich!", kennung
				.equals(neueVorgangsmappe.getCasefileId()));
		Assert.assertTrue(neueVorgangsmappe.getCasefileId().hasExtension());
		Assert.assertEquals(CasefileId.valueOf(kennung,
				"Horst Senkel"), neueVorgangsmappe.getCasefileId());
	}

	public void testLocalToString() {
		CasefileId kennung = null;
		kennung = CasefileId.createNew();

		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		Assert.assertEquals(kennung.toString(), vorgangsmappe
				.getCasefileId().toString());
	}

	@Test
	public void testMappenkennung() throws UnknownHostException {
		final CasefileId kennung = CasefileId.createNew();
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		final CasefileId kennungAusDerMappe = vorgangsmappe
				.getCasefileId();
		Assert.assertNotNull(kennungAusDerMappe);
		Assert.assertEquals(kennung, kennungAusDerMappe);
	}

	@Override
	protected MessageCasefile getNewInstanceOfClassUnderTest() {
		CasefileId kennung = null;
		kennung = CasefileId.createNew();

		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		return new MessageCasefile(kennung, alarmNachricht);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected MessageCasefile[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		CasefileId kennung1 = null;
		CasefileId kennung2 = null;
		CasefileId kennung3 = null;
		kennung1 = CasefileId.createNew();
		kennung2 = CasefileId.createNew();
		kennung3 = CasefileId.createNew();

		final AlarmMessage alarmNachricht1 = new AlarmMessage(
				"Test-Nachricht 1");
		final AlarmMessage alarmNachricht2 = new AlarmMessage(
				"Test-Nachricht 2");
		final AlarmMessage alarmNachricht3 = new AlarmMessage(
				"Test-Nachricht 3");
		return new MessageCasefile[] {
				new MessageCasefile(kennung1, alarmNachricht1),
				new MessageCasefile(kennung2, alarmNachricht2),
				new MessageCasefile(kennung3, alarmNachricht3) };
	}
}
