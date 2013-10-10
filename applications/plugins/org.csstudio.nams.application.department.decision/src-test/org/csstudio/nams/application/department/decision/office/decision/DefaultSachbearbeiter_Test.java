package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.decision.Vorgangsmappenkennung;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.yaams.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.Regel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultSachbearbeiter_Test {

	
	private StandardAblagekorb<Vorgangsmappe> ausgangskorb;
	private ExecutorBeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb;
	private TestRegel regel;
	private Regelwerkskennung regelwerksKennung;

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
		ausgangskorb = new StandardAblagekorb<Vorgangsmappe>();
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<Vorgangsmappe>(new DirectExecutor());
		regelwerksKennung = Regelwerkskennung.valueOf();
		regel = new TestRegel();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private DefaultSachbearbeiter erzeugeSachbearbeiter() {
		return new DefaultSachbearbeiter(eingangskorb,ausgangskorb, new DefaultRegelwerk(regelwerksKennung, regel));
	}

	@Test
	public void testBeginneArbeit() {
		DefaultSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter();
		
		assertFalse(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beginneArbeit();
		assertTrue(sachbearbeiter.istAmArbeiten());
		sachbearbeiter.beendeArbeit();
		assertFalse(sachbearbeiter.istAmArbeiten());
	}

	@SuppressWarnings("deprecation")
	@Test(timeout=1000)
	public void testHandleNachricht() throws UnknownHostException, InterruptedException {
		DefaultSachbearbeiter sachbearbeiter = erzeugeSachbearbeiter();
		sachbearbeiter.beginneArbeit();
		
		Vorgangsmappe vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("XXX"));
		
		regel.setResult(false);
		
		eingangskorb.ablegen(vorgangsmappe);
		Vorgangsmappe aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		vorgangsmappe = new Vorgangsmappe(Vorgangsmappenkennung.createNew(InetAddress.getLocalHost(), new Date()), new AlarmNachricht("XXX"));
		
		regel.setResult(true);
		
		eingangskorb.ablegen(vorgangsmappe);
		aeltesterEingang = ausgangskorb.entnehmeAeltestenEingang();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
	}
}