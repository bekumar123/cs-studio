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
		final CasefileId kennung = CasefileId.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile mappe = new MessageCasefile(kennung, alarmNachricht);
		Assert
				.assertFalse("mappe.istAbgeschlossen()", mappe
						.istAbgeschlossen());
		final CasefileId abschliesserKennung = CasefileId
				.valueOf(InetAddress.getByAddress(new byte[] { 127, 0, 0, 3 }),
						new Date(123457));
		mappe.pruefungAbgeschlossenDurch(abschliesserKennung);
		Assert.assertTrue("mappe.istAbgeschlossen()", mappe.istAbgeschlossen());
		Assert
				.assertTrue(
						"abschliesserKennung.equals(mappe.gibAbschliessendeMappenkennung())",
						abschliesserKennung.equals(mappe
								.gibAbschliessendeMappenkennung()));
	}

	@Test
	public void testGibAusloesendeAlarmNachrichtDiesesVorganges()
			throws UnknownHostException {
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final CasefileId vorgangsmappenkennung = CasefileId
				.valueOf(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(123456));
		final MessageCasefile vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		Assert.assertSame(
				"Hineingereichte Nachricht ist auch die, die herauskommt",
				alarmNachricht, vorgangsmappe
						.getAlarmMessage());
	}

	@Test
	public void testKopieren() throws UnknownHostException {
		final CasefileId kennung = CasefileId.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");

		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		final MessageCasefile neueVorgangsmappe = vorgangsmappe
				.erstelleKopieFuer("Horst Senkel");

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
				kennung == neueVorgangsmappe.gibMappenkennung());
		Assert.assertFalse("Kennung bleibt nicht gleich!", kennung
				.equals(neueVorgangsmappe.gibMappenkennung()));
		Assert.assertTrue(neueVorgangsmappe.gibMappenkennung().hatErgaenzung());
		Assert.assertEquals(CasefileId.valueOf(kennung,
				"Horst Senkel"), neueVorgangsmappe.gibMappenkennung());
	}

	public void testLocalToString() {
		CasefileId kennung = null;
		try {
			kennung = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		Assert.assertEquals(kennung.toString(), vorgangsmappe
				.gibMappenkennung().toString());
	}

	@Test
	public void testMappenkennung() throws UnknownHostException {
		final CasefileId kennung = CasefileId.valueOf(
				InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }),
				new Date(123456));
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"Test-Nachricht");
		final MessageCasefile vorgangsmappe = new MessageCasefile(kennung,
				alarmNachricht);

		final CasefileId kennungAusDerMappe = vorgangsmappe
				.gibMappenkennung();
		Assert.assertNotNull(kennungAusDerMappe);
		Assert.assertEquals(kennung, kennungAusDerMappe);
	}

	@Override
	protected MessageCasefile getNewInstanceOfClassUnderTest() {
		CasefileId kennung = null;
		try {
			kennung = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

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
		try {
			kennung1 = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
			kennung2 = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
			kennung3 = CasefileId.valueOf(InetAddress
					.getByAddress(new byte[] { 127, 0, 0, 1 }),
					new Date(123456));
		} catch (final UnknownHostException e) {
			Assert.fail(e.getMessage());
		}

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
