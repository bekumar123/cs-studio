package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk.TimeoutType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimebasedSachbearbeiter_Test {

	
	private DefaultDocumentBox<MessageCasefile> ausgangskorb;
	private ObservableInbox<Document> eingangskorb;
	private TestRegel startRegel;
	private Regelwerkskennung regelwerksKennung;
	private DefaultDocumentBox<MessageCasefile> offeneVorgaenge;
	private DefaultDocumentBox<Terminnotiz> terminKorb;
	private TestRegel stopRegel;

	private class DirectExecutor implements Executor {
	     public void execute(Runnable r) {
	         r.run();
	     }
	}

	private class TestRegel implements Regel {

		private boolean result = false;

		@Override
		public boolean pruefeNachricht(AlarmMessage nachricht) {
			return result;
		}

		@Override
		public boolean pruefeNachricht(AlarmMessage nachricht,
				AlarmMessage vergleichsNachricht) {
			return result;
		}
		
		public void setResult(boolean result) {
			this.result = result;
		}
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<Document>(new DirectExecutor());
		ausgangskorb = new DefaultDocumentBox<MessageCasefile>();
		offeneVorgaenge = new DefaultDocumentBox<MessageCasefile>();
		terminKorb = new DefaultDocumentBox<Terminnotiz>();
		regelwerksKennung = Regelwerkskennung.valueOf();
		startRegel = new TestRegel();
		stopRegel = new TestRegel();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private TimebasedFilterWorker erzeugeSachbearbeiter(TimeoutType timeoutType) {
		return new TimebasedFilterWorker("name", eingangskorb, offeneVorgaenge, terminKorb, ausgangskorb, new TimebasedRegelwerk(regelwerksKennung, startRegel, stopRegel, Milliseconds.valueOf(10), timeoutType));
	}

	@Test
	public void testBeginneArbeit() {
		TimebasedFilterWorker sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_TIMEOUT);
		
		assertFalse(sachbearbeiter.isWorking());
		sachbearbeiter.startWorking();
		assertTrue(sachbearbeiter.isWorking());
		sachbearbeiter.stopWorking();
		assertFalse(sachbearbeiter.isWorking());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testHandleNachrichtSendeBeiTimeout() throws UnknownHostException, InterruptedException {
		TimebasedFilterWorker sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_TIMEOUT);
		sachbearbeiter.startWorking();
		
		// Test Startregel trifft nicht zu
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(false);
		
		eingangskorb.put(vorgangsmappe);
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		
		// Test SENDE_BEI_TIMEOUT
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(true);
		
		eingangskorb.put(vorgangsmappe);
		eingangskorb.put(terminKorb.takeDocument());
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertTrue(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test cancel bei Stopnachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
				"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.put(vorgangsmappe);
		
		MessageCasefile stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		eingangskorb.put(stopVorgangsmappe);
		
		eingangskorb.put(terminKorb.takeDocument());
		
		aeltesterEingang = ausgangskorb.takeDocument();

		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());

		aeltesterEingang = ausgangskorb.takeDocument();

		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());

		// Test andere Nachricht zwischendurch
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.put(vorgangsmappe);
		
		stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		eingangskorb.put(stopVorgangsmappe);
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testHandleNachrichtSendeBeiStopRegel() throws UnknownHostException, InterruptedException {
		TimebasedFilterWorker sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_STOP_REGEL);
		sachbearbeiter.startWorking();
		
		// Test Startregel trifft nicht zu
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(false);
		
		eingangskorb.put(vorgangsmappe);
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		
		// Test cancel bei Timeout
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(true);
		
		eingangskorb.put(vorgangsmappe);
		eingangskorb.put(terminKorb.takeDocument());
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertTrue(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test sende bei Stopnachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.put(vorgangsmappe);
		
		MessageCasefile stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		eingangskorb.put(stopVorgangsmappe);
		
		eingangskorb.put(terminKorb.takeDocument());
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test andere Nachricht zwischendurch
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.put(vorgangsmappe);
		
		stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		eingangskorb.put(stopVorgangsmappe);
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
	}
}