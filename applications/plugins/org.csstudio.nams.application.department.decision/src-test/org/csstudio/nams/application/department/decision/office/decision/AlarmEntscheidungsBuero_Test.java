package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk.TimeoutType;
import org.junit.Test;

public class AlarmEntscheidungsBuero_Test extends TestCase {

	public void testConstructor() {
		final int ANZAHL_REGELWERKE = 3;

		final Regelwerk[] regelwerke = new Regelwerk[ANZAHL_REGELWERKE];
		for (int i = 0; i < ANZAHL_REGELWERKE; i++) {
			regelwerke[i] = new DefaultRegelwerk(Regelwerkskennung.valueOf(),
					new Regel() {
						// Impl hier egal!
						@Override
						public boolean pruefeNachricht(AlarmMessage nachricht) {
							// TODO Auto-generated method stub
							return false;
						}
						@Override
						public boolean pruefeNachricht(
								AlarmMessage nachricht,
								AlarmMessage vergleichsNachricht) {
							// TODO Auto-generated method stub
							return false;
						}
					});
		}

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), regelwerke,
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 10);

		Assert.assertNotNull(buero.gibAbteilungsleiterFuerTest());
		Assert.assertNotNull(buero.gibAssistenzFuerTest());
		final Collection<Arbeitsfaehig> listOfSachbearbeiter = buero
				.gibListeDerSachbearbeiterFuerTest();
		Assert.assertNotNull(listOfSachbearbeiter);
		Assert.assertTrue("listOfSachbearbeiter.size()==" + ANZAHL_REGELWERKE,
				listOfSachbearbeiter.size() == ANZAHL_REGELWERKE);

		Assert.assertTrue("buero.getAbteilungsleiter().istAmArbeiten()", buero
				.gibAbteilungsleiterFuerTest().isWorking());
		Assert.assertTrue("buero.getAssistenz().istAmArbeiten()", buero
				.gibAssistenzFuerTest().isWorking());
		for (final Arbeitsfaehig bearbeiter : listOfSachbearbeiter) {
			Assert.assertTrue(
					"buero.getListOfSachbearbeiter().istAmArbeiten()",
					bearbeiter.isWorking());
		}
	}

	public void testIntegration() throws InterruptedException,
			UnknownHostException {
		final Regel regel = new Regel() {
						@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return true;
			}
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				Assert.fail();
				return false;
			}
		};
		
		final Regel regel2 = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return false;
			}

			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				Assert.fail();
				return false;
			}
		};
		
		final Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf();
		final Regelwerk regelwerk = new DefaultRegelwerk(regelwerkskennung,
				regel);

		final Regelwerkskennung regelwerkskennung2 = Regelwerkskennung
				.valueOf();
		final Regelwerk regelwerk2 = new DefaultRegelwerk(regelwerkskennung2,
				regel2);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), new Regelwerk[] { regelwerk,
						regelwerk2 },
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1);
		
		final Inbox<MessageCasefile> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final DefaultDocumentBox<MessageCasefile> alarmVorgangAusgangskorb = (DefaultDocumentBox<MessageCasefile>) buero
				.gibAlarmVorgangAusgangskorb();

		final CasefileId vorgangsmappenkennung = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		final AlarmMessage alarmNachricht = new AlarmMessage(
				"test nachricht");
		final MessageCasefile vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.put(vorgangsmappe);

		MessageCasefile aelteste = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertEquals(alarmNachricht, aelteste.getAlarmNachricht());
		Assert.assertTrue(aelteste.getWeiteresVersandVorgehen() == WeiteresVersandVorgehen.VERSENDEN);
		Assert.assertTrue(aelteste.getBearbeitetMitRegelWerk() == regelwerk.getRegelwerksKennung());

		aelteste = alarmVorgangAusgangskorb.takeDocument();

		Assert.assertEquals(alarmNachricht, aelteste.getAlarmNachricht());
		Assert.assertTrue(aelteste.getWeiteresVersandVorgehen() == WeiteresVersandVorgehen.NICHT_VERSENDEN);
		Assert.assertTrue(aelteste.getBearbeitetMitRegelWerk() == regelwerk2.getRegelwerksKennung());		
	}

	@Test(timeout = 4000)
	public void testTimeBasedAufhebenBeiTimeout() throws Throwable {
		Regel startRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				Assert.fail();
				return false;
			}
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return nachricht.gibNachrichtenText().equals("START");
			}
		};
		Regel stopRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				return pruefeNachricht(nachricht);
			}
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return nachricht.gibNachrichtenText().equals("STOP");
			}
		};
		TimebasedRegelwerk timebasedRegelwerk = new TimebasedRegelwerk(Regelwerkskennung.valueOf(), startRegel, stopRegel, Milliseconds.valueOf(100), TimeoutType.SENDE_BEI_STOP_REGEL);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), new Regelwerk[] { timebasedRegelwerk },
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1);
		final Inbox<MessageCasefile> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final DefaultDocumentBox<MessageCasefile> alarmVorgangAusgangskorb = (DefaultDocumentBox<MessageCasefile>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		CasefileId vorgangsmappenkennung = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht = new AlarmMessage("XXO");
		MessageCasefile vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.put(vorgangsmappe);

		// Passende 1
		CasefileId vorgangsmappenkennung2 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht2 = new AlarmMessage("START");
		MessageCasefile vorgangsmappe2 = new MessageCasefile(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.put(vorgangsmappe2);

		// Un-Passende 1
		CasefileId vorgangsmappenkennung3 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht3 = new AlarmMessage("Baeh!");
		MessageCasefile vorgangsmappe3 = new MessageCasefile(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.put(vorgangsmappe3);

		// Passende Bestaetigung1
		CasefileId vorgangsmappenkennung4 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht4 = new AlarmMessage("STOP");
		MessageCasefile vorgangsmappe4 = new MessageCasefile(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.put(vorgangsmappe4);

		// Pruefen 1
		MessageCasefile aelteste = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.takeDocument();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		MessageCasefile vorgangsmappe5 = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

		
		
		
		
		
		

		// Un-Passende 1
		vorgangsmappenkennung = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht = new AlarmMessage("XXO");
		vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.put(vorgangsmappe);

		// Passende 1
		vorgangsmappenkennung2 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht2 = new AlarmMessage("START");
		vorgangsmappe2 = new MessageCasefile(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.put(vorgangsmappe2);

		// Un-Passende 1
		vorgangsmappenkennung3 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht3 = new AlarmMessage("Baeh!");
		vorgangsmappe3 = new MessageCasefile(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.put(vorgangsmappe3);

		// Passende Bestaetigung1
		Thread.sleep(150);
		vorgangsmappenkennung4 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht4 = new AlarmMessage("STOP");
		vorgangsmappe4 = new MessageCasefile(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.put(vorgangsmappe4);

		// Pruefen 1
		aelteste = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.takeDocument();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		vorgangsmappe5 = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());
		
	}
	
	@Test(timeout = 4000)
	public void testTimeBasedAusloesenBeiTimeout() throws Throwable {
		Regel startRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				Assert.fail();
				return false;
			}
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return nachricht.gibNachrichtenText().equals("START");
			}
		};
		Regel stopRegel = new Regel() {
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht,
					AlarmMessage vergleichsNachricht) {
				return pruefeNachricht(nachricht);
			}
			@Override
			public boolean pruefeNachricht(AlarmMessage nachricht) {
				return nachricht.gibNachrichtenText().equals("STOP");
			}
		};
		TimebasedRegelwerk timebasedRegelwerk = new TimebasedRegelwerk(Regelwerkskennung.valueOf(), startRegel, stopRegel, Milliseconds.valueOf(100), TimeoutType.SENDE_BEI_TIMEOUT);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), new Regelwerk[] { timebasedRegelwerk },
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1);
		final Inbox<MessageCasefile> alarmVorgangEingangskorb = buero
				.gibAlarmVorgangEingangskorb();
		final DefaultDocumentBox<MessageCasefile> alarmVorgangAusgangskorb = (DefaultDocumentBox<MessageCasefile>) buero
				.gibAlarmVorgangAusgangskorb();

		// Un-Passende 1
		CasefileId vorgangsmappenkennung = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht = new AlarmMessage("XXO");
		MessageCasefile vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.put(vorgangsmappe);

		// Passende 1
		CasefileId vorgangsmappenkennung2 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht2 = new AlarmMessage("START");
		MessageCasefile vorgangsmappe2 = new MessageCasefile(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.put(vorgangsmappe2);

		// Un-Passende 1
		CasefileId vorgangsmappenkennung3 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht3 = new AlarmMessage("Baeh!");
		MessageCasefile vorgangsmappe3 = new MessageCasefile(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.put(vorgangsmappe3);

		// Passende Bestaetigung1
		CasefileId vorgangsmappenkennung4 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		AlarmMessage alarmNachricht4 = new AlarmMessage("STOP");
		MessageCasefile vorgangsmappe4 = new MessageCasefile(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.put(vorgangsmappe4);

		// Pruefen 1
		MessageCasefile aelteste = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.takeDocument();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		MessageCasefile vorgangsmappe5 = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

		
		
		
		
		
		

		// Un-Passende 1
		vorgangsmappenkennung = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht = new AlarmMessage("XXO");
		vorgangsmappe = new MessageCasefile(
				vorgangsmappenkennung, alarmNachricht);
		alarmVorgangEingangskorb.put(vorgangsmappe);

		// Passende 1
		vorgangsmappenkennung2 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht2 = new AlarmMessage("START");
		vorgangsmappe2 = new MessageCasefile(
				vorgangsmappenkennung2, alarmNachricht2);
		alarmVorgangEingangskorb.put(vorgangsmappe2);

		// Un-Passende 1
		vorgangsmappenkennung3 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht3 = new AlarmMessage("Baeh!");
		vorgangsmappe3 = new MessageCasefile(
				vorgangsmappenkennung3, alarmNachricht3);
		alarmVorgangEingangskorb.put(vorgangsmappe3);

		// Passende Bestaetigung1
		Thread.sleep(150);
		vorgangsmappenkennung4 = CasefileId
				.createNew(InetAddress.getLocalHost(), new Date());
		alarmNachricht4 = new AlarmMessage("STOP");
		vorgangsmappe4 = new MessageCasefile(
				vorgangsmappenkennung4, alarmNachricht4);
		alarmVorgangEingangskorb.put(vorgangsmappe4);

		// Pruefen 1
		aelteste = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("XXO", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 3
		aelteste = alarmVorgangAusgangskorb.takeDocument();

		Assert.assertNotNull(aelteste);
		Assert.assertEquals("Baeh!", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Nachricht 2
		aelteste = alarmVorgangAusgangskorb.takeDocument();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		// Pruefen 4
		vorgangsmappe5 = alarmVorgangAusgangskorb
				.takeDocument();

		Assert.assertNotNull(vorgangsmappe5);
		Assert.assertEquals("STOP", vorgangsmappe5
				.getAlarmNachricht()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, vorgangsmappe5.getWeiteresVersandVorgehen());

	}
}
