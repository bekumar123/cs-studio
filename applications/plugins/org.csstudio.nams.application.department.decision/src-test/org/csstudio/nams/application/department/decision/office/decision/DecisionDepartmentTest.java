package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.Worker;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter.TimeoutType;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.junit.Test;

public class DecisionDepartmentTest extends TestCase {

	public void testConstructor() {
		final int ANZAHL_REGELWERKE = 3;

		final List<Filter> regelwerke = new ArrayList<Filter>(ANZAHL_REGELWERKE);
		for (int i = 0; i < ANZAHL_REGELWERKE; i++) {
			regelwerke.add(new DefaultFilter(FilterId.valueOf(i), new FilterCondition() {
				// Impl hier egal!
				@Override
				public boolean pruefeNachricht(AlarmMessage nachricht) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
					// TODO Auto-generated method stub
					return false;
				}
			}));
		}

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), regelwerke,
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 10, new LoggerMock());

		Assert.assertNotNull(buero.gibAbteilungsleiterFuerTest());
		Assert.assertNotNull(buero.gibAssistenzFuerTest());
		final Collection<Worker> listOfSachbearbeiter = buero
				.gibListeDerSachbearbeiterFuerTest();
		Assert.assertNotNull(listOfSachbearbeiter);
		Assert.assertTrue("listOfSachbearbeiter.size()==" + ANZAHL_REGELWERKE,
				listOfSachbearbeiter.size() == ANZAHL_REGELWERKE);

		Assert.assertTrue("buero.getAbteilungsleiter().istAmArbeiten()", buero
				.gibAbteilungsleiterFuerTest().isWorking());
		Assert.assertTrue("buero.getAssistenz().istAmArbeiten()", buero
				.gibAssistenzFuerTest().isWorking());
		for (final Worker bearbeiter : listOfSachbearbeiter) {
			Assert.assertTrue(
					"buero.getListOfSachbearbeiter().istAmArbeiten()",
					bearbeiter.isWorking());
		}
	}

	@Test(timeout = 4000)
	public void testIntegration() throws InterruptedException,
			UnknownHostException {
		final FilterCondition regel = new FilterCondition() {
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
		
		final FilterCondition regel2 = new FilterCondition() {
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
		
		final FilterId regelwerkskennung = FilterId.valueOf(1);
		final Filter regelwerk = new DefaultFilter(regelwerkskennung,
				regel);

		final FilterId regelwerkskennung2 = FilterId.valueOf(2);
		final Filter regelwerk2 = new DefaultFilter(regelwerkskennung2,
				regel2);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), Arrays.asList(new Filter[]{ regelwerk,
						regelwerk2 }),
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1, new LoggerMock());
		
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

		Assert.assertEquals(alarmNachricht, aelteste.getAlarmMessage());
		Assert.assertTrue(aelteste.getWeiteresVersandVorgehen() == WeiteresVersandVorgehen.VERSENDEN);
		Assert.assertTrue(aelteste.getHandledByFilterId() == regelwerk.getFilterId());

		Assert.assertEquals(alarmVorgangAusgangskorb.documentCount(), 0);
	}

	@Test(timeout = 4000)
	public void testTimeBasedAufhebenBeiTimeout() throws Throwable {
		FilterCondition startRegel = new FilterCondition() {
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
		FilterCondition stopRegel = new FilterCondition() {
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
		TimebasedFilter timebasedRegelwerk = new TimebasedFilter(FilterId.valueOf(666), startRegel, stopRegel, Milliseconds.valueOf(100), TimeoutType.SENDE_BEI_STOP_REGEL);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), Arrays.asList(new Filter[] { timebasedRegelwerk }),
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1, new LoggerMock());
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
		Assert.assertEquals("START", aelteste
				.getAlarmMessage()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		Assert.assertEquals(0, alarmVorgangAusgangskorb.documentCount());

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

		Assert.assertEquals(0, alarmVorgangAusgangskorb.documentCount());
	}
	
	@Test(timeout = 4000)
	public void testTimeBasedAusloesenBeiTimeout() throws Throwable {
		FilterCondition startRegel = new FilterCondition() {
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
		FilterCondition stopRegel = new FilterCondition() {
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
		TimebasedFilter timebasedRegelwerk = new TimebasedFilter(FilterId.valueOf(18), startRegel, stopRegel, Milliseconds.valueOf(100), TimeoutType.SENDE_BEI_TIMEOUT);

		final DecisionDepartment buero = new DecisionDepartment(
				new DefaultExecutionService(), Arrays.asList(new Filter[] { timebasedRegelwerk }),
				new DefaultDocumentBox<MessageCasefile>(),
				new DefaultDocumentBox<MessageCasefile>(), 1, new LoggerMock());
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

		Assert.assertEquals(0, alarmVorgangAusgangskorb.documentCount());

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


		// Eine Alarmnachricht
		MessageCasefile aelteste = alarmVorgangAusgangskorb.takeDocument();
		
		Assert.assertNotNull(aelteste);
		Assert.assertEquals("START", aelteste
				.getAlarmMessage()
				.gibNachrichtenText());
		Assert.assertEquals(WeiteresVersandVorgehen.VERSENDEN, aelteste.getWeiteresVersandVorgehen());

		Assert.assertEquals(0, alarmVorgangAusgangskorb.documentCount());
	}
}
