package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.Regel;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultSachbearbeiter_Test {

	
	private DefaultDocumentBox<MessageCasefile> ausgangskorb;
	private ExecutorBeobachtbarerEingangskorb<MessageCasefile> eingangskorb;
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
		ausgangskorb = new DefaultDocumentBox<MessageCasefile>();
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<MessageCasefile>(new DirectExecutor());
		regelwerksKennung = Regelwerkskennung.valueOf();
		regel = new TestRegel();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private DefaultFilterWorker erzeugeSachbearbeiter() {
		return new DefaultFilterWorker(eingangskorb,ausgangskorb, new DefaultRegelwerk(regelwerksKennung, regel));
	}

	@Test
	public void testBeginneArbeit() {
		DefaultFilterWorker sachbearbeiter = erzeugeSachbearbeiter();
		
		assertFalse(sachbearbeiter.isWorking());
		sachbearbeiter.startWorking();
		assertTrue(sachbearbeiter.isWorking());
		sachbearbeiter.stopWorking();
		assertFalse(sachbearbeiter.isWorking());
	}

	@SuppressWarnings("deprecation")
	@Test(timeout=1000)
	public void testHandleNachricht() throws UnknownHostException, InterruptedException {
		DefaultFilterWorker sachbearbeiter = erzeugeSachbearbeiter();
		sachbearbeiter.startWorking();
		
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("XXX"));
		
		regel.setResult(false);
		
		eingangskorb.put(vorgangsmappe);
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.NICHT_VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("XXX"));
		
		regel.setResult(true);
		
		eingangskorb.put(vorgangsmappe);
		aeltesterEingang = ausgangskorb.takeDocument();
		
		assertEquals(vorgangsmappe, aeltesterEingang);
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(regelwerksKennung, aeltesterEingang.getBearbeitetMitRegelWerk());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
	}
}