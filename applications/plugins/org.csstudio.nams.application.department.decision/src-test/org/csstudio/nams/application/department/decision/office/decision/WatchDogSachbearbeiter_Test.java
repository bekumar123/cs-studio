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
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.WatchDogRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WatchDogSachbearbeiter_Test {

	
	private static final int DELAY = 10;
	private StandardAblagekorb<Vorgangsmappe> ausgangskorb;
	private ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb;
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
		public boolean pruefeNachricht(AlarmNachricht nachricht) {
			return result;
		}

		@Override
		public boolean pruefeNachricht(AlarmNachricht nachricht,
				AlarmNachricht vergleichsNachricht) {
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
		ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		regelwerksKennung = Regelwerkskennung.valueOf();
		regel = new TestRegel();
		timer = new TestTimer();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private WatchDogSachbearbeiter erzeugeSachbearbeiter() {
		return new WatchDogSachbearbeiter(eingangskorb,ausgangskorb, new WatchDogRegelwerk(regelwerksKennung, regel, Millisekunden.valueOf(DELAY)), timer);
	}

	@Test
	public void testBeginneArbeit() {
		WatchDogSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter();
		
		assertFalse(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beginneArbeit();
		assertTrue(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beendeArbeit();
		assertFalse(sachbearbeiter.istAmArbeiten());
	}

	@SuppressWarnings("deprecation")
	@Test(timeout=1000)
	public void testHandleNachricht() throws UnknownHostException, InterruptedException {
		WatchDogSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter();
		// timer setzen bei beginn des WachtDog
		assertNull(timer.getLastScheduledTask());
		sachbearbeiter.beginneArbeit();
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());

		// Alarmnachricht bei ablauf des timers
		TimerTask task = timer.getLastScheduledTask();
		timer.reset();
		task.run();
		assertNull("Nach ablauf des timers darf kein neuer gestartet werden", timer.getLastScheduledTask());
		
		Vorgangsmappe aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// WatchDog startet timer neu bei eingang von gültiger Nachricht
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("XXX"));
		regel.setResult(true);
		eingangskorb.ablegen(vorgangsmappe);
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		timer.reset();
		// WatchDog startet timer neu bei eingang von gültiger Nachricht
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("XXX"));
		regel.setResult(true);
		eingangskorb.ablegen(vorgangsmappe);
		assertNotNull(timer.getLastScheduledTask());
		assertEquals(DELAY, timer.getLastScheduledDelay());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		timer.reset();
		// WatchDog startet timer nicht neu bei eingang von ungültiger Nachricht
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("XXX"));
		regel.setResult(false);
		eingangskorb.ablegen(vorgangsmappe);
		assertNull(timer.getLastScheduledTask());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
	}
}