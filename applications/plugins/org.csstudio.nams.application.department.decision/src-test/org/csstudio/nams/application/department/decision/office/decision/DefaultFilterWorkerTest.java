package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.*;

import java.util.concurrent.Executor;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DefaultFilterWorkerTest {

	
	private DefaultDocumentBox<MessageCasefile> ausgangskorb;
	private ExecutorBeobachtbarerEingangskorb<MessageCasefile> eingangskorb;
	private TestFilterCondition regel;
	private FilterId regelwerksKennung;

	private class DirectExecutor implements Executor {
	     public void execute(Runnable r) {
	         r.run();
	     }
	}

	private class TestFilterCondition implements FilterCondition {

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

	@Before
	public void setUp() throws Exception {
		ausgangskorb = new DefaultDocumentBox<MessageCasefile>();
		eingangskorb = new ExecutorBeobachtbarerEingangskorb<MessageCasefile>(new DirectExecutor());
		regelwerksKennung = FilterId.valueOf(1);
		regel = new TestFilterCondition();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private DefaultFilterWorker erzeugeSachbearbeiter() {
		return new DefaultFilterWorker(eingangskorb,ausgangskorb, new DefaultFilter(regelwerksKennung, regel));
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
	public void testHandleNachricht() throws InterruptedException {
		DefaultFilterWorker sachbearbeiter = erzeugeSachbearbeiter();
		sachbearbeiter.startWorking();
		
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(), new AlarmMessage("XXX"));
		
		regel.setResult(false);
		
		eingangskorb.put(vorgangsmappe);

		assertEquals(0, ausgangskorb.documentCount());
		
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(), new AlarmMessage("XXX"));
		
		regel.setResult(true);
		
		eingangskorb.put(vorgangsmappe);
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		assertFalse(vorgangsmappe.equals(aeltesterEingang));
		assertEquals(regelwerksKennung, aeltesterEingang.getHandledByFilterId());
		assertTrue(aeltesterEingang.isClosed());
		assertFalse(aeltesterEingang.isClosedByTimeout());
		
	}
}