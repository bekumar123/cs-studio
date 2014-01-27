package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk.TimeoutType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimebasedSachbearbeiter_Test {

	
	private StandardAblagekorb<Vorgangsmappe> ausgangskorb;
	private BeobachtbarerEingangskorb<Ablagefaehig> eingangskorb;
	private TestRegel startRegel;
	private Regelwerkskennung regelwerksKennung;
	private StandardAblagekorb<Vorgangsmappe> offeneVorgaenge;
	private StandardAblagekorb<Terminnotiz> terminKorb;
	private TestRegel stopRegel;

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

	@SuppressWarnings("deprecation")
	@Before
	public void setUp() throws Exception {
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<Ablagefaehig>(new DirectExecutor());
		ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		offeneVorgaenge = new StandardAblagekorb<Vorgangsmappe>();
		terminKorb = new StandardAblagekorb<Terminnotiz>();
		regelwerksKennung = Regelwerkskennung.valueOf();
		startRegel = new TestRegel();
		stopRegel = new TestRegel();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private TimebasedSachbearbeiter erzeugeSachbearbeiter(TimeoutType timeoutType) {
		return new TimebasedSachbearbeiter("name", eingangskorb, offeneVorgaenge, terminKorb, ausgangskorb, new TimebasedRegelwerk(regelwerksKennung, startRegel, stopRegel, Millisekunden.valueOf(10), timeoutType));
	}

	@Test
	public void testBeginneArbeit() {
		TimebasedSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_TIMEOUT);
		
		assertFalse(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beginneArbeit();
		assertTrue(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beendeArbeit();
		assertFalse(sachbearbeiter.istAmArbeiten());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testHandleNachrichtSendeBeiTimeout() throws UnknownHostException, InterruptedException {
		TimebasedSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_TIMEOUT);
		sachbearbeiter.beginneArbeit();
		
		// Test Startregel trifft nicht zu
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("start message"));
		
		startRegel.setResult(false);
		
		eingangskorb.ablegen(vorgangsmappe);
		Vorgangsmappe aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		
		// Test SENDE_BEI_TIMEOUT
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("start message"));
		
		startRegel.setResult(true);
		
		eingangskorb.ablegen(vorgangsmappe);
		eingangskorb.ablegen(terminKorb.entnehmeAeltestenEingang());
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertTrue(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test cancel bei Stopnachricht
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
				"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.ablegen(vorgangsmappe);
		
		Vorgangsmappe stopVorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		eingangskorb.ablegen(stopVorgangsmappe);
		
		eingangskorb.ablegen(terminKorb.entnehmeAeltestenEingang());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();

		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());

		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();

		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());

		// Test andere Nachricht zwischendurch
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.ablegen(vorgangsmappe);
		
		stopVorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		eingangskorb.ablegen(stopVorgangsmappe);
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
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
		TimebasedSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter(TimeoutType.SENDE_BEI_STOP_REGEL);
		sachbearbeiter.beginneArbeit();
		
		// Test Startregel trifft nicht zu
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("start message"));
		
		startRegel.setResult(false);
		
		eingangskorb.ablegen(vorgangsmappe);
		Vorgangsmappe aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		
		// Test cancel bei Timeout
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("start message"));
		
		startRegel.setResult(true);
		
		eingangskorb.ablegen(vorgangsmappe);
		eingangskorb.ablegen(terminKorb.entnehmeAeltestenEingang());
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertTrue(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test sende bei Stopnachricht
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.ablegen(vorgangsmappe);
		
		Vorgangsmappe stopVorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		eingangskorb.ablegen(stopVorgangsmappe);
		
		eingangskorb.ablegen(terminKorb.entnehmeAeltestenEingang());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test andere Nachricht zwischendurch
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		eingangskorb.ablegen(vorgangsmappe);
		
		stopVorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmNachricht(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		eingangskorb.ablegen(stopVorgangsmappe);
		
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(stopVorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung,
				aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
	}
}