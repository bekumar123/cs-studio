package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.WatchDogRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WatchDogSachbearbeiter_Test {

	
	private static final int DELAY = 10;
	private DefaultDocumentBox<MessageCasefile> ausgangskorb;
	private ExecutorBeobachtbarerEingangskorb<MessageCasefile> eingangskorb;
	private TestRegel regel;
	private Regelwerkskennung regelwerksKennung;
	private TestTimer timer;

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
	
	private class TestTimer extends Timer {
		private TimerTask task;
		private long delay = -1;

		@Override
		public void schedule(TimerTask task, long delay) {
			this.task = task;
			this.delay = delay;
		}
		
		public TimerTask getLastScheduledTask() {
			return task;
		}
		
		public long getLastScheduledDelay() {
			return delay;
		}
		
		public void reset() {
			task = null;
			delay = -1;
		}
	}

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		ausgangskorb = new DefaultDocumentBox<MessageCasefile>();
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<MessageCasefile>(new DirectExecutor());
		regelwerksKennung = Regelwerkskennung.valueOf();
		regel = new TestRegel();
		timer = new TestTimer();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private WatchDogFilterWorker erzeugeSachbearbeiter() {
		return new WatchDogFilterWorker(eingangskorb,ausgangskorb, new WatchDogRegelwerk(regelwerksKennung, regel, Milliseconds.valueOf(DELAY)), timer);
	}

	@Test
	public void testBeginneArbeit() {
		WatchDogFilterWorker sachbearbeiter = erzeugeSachbearbeiter();
		
		assertFalse(sachbearbeiter.isWorking());
		sachbearbeiter.startWorking();
		assertTrue(sachbearbeiter.isWorking());
		sachbearbeiter.stopWorking();
		assertFalse(sachbearbeiter.isWorking());
	}

	@SuppressWarnings("deprecation")
	@Test(timeout=1000)
	public void testHandleNachricht() throws UnknownHostException, InterruptedException {
		WatchDogFilterWorker sachbearbeiter = erzeugeSachbearbeiter();
		// timer setzen bei beginn des WachtDog
		assertNull(timer.getLastScheduledTask());
		sachbearbeiter.startWorking();
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());

		// Alarmnachricht bei ablauf des timers
		TimerTask task = timer.getLastScheduledTask();
		timer.reset();
		task.run();
		assertNull("Nach ablauf des timers darf kein neuer gestartet werden", timer.getLastScheduledTask());
		
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// WatchDog startet timer neu bei eingang von gültiger Nachricht
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("XXX"));
		regel.setResult(true);
		eingangskorb.put(vorgangsmappe);
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		timer.reset();
		// WatchDog startet timer neu bei eingang von gültiger Nachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("XXX"));
		regel.setResult(true);
		eingangskorb.put(vorgangsmappe);
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		timer.reset();
		// WatchDog startet timer nicht neu bei eingang von ungültiger Nachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("XXX"));
		regel.setResult(false);
		eingangskorb.put(vorgangsmappe);
		assertNull(timer.getLastScheduledTask());
		
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
	}
}