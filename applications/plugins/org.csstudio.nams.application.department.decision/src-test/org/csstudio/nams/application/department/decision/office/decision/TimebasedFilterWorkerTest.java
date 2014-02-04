package org.csstudio.nams.application.department.decision.office.decision;

import static junit.framework.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.Executor;

import junit.framework.Assert;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.ExecutorBeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter.TimeoutType;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TimebasedFilterWorkerTest {

	
	private DefaultDocumentBox<MessageCasefile> ausgangskorb;
	private ObservableInbox<Document> filterInbox;
	private TestRegel startRegel;
	private FilterId filterId;
	private DefaultDocumentBox<MessageCasefile> offeneVorgaenge;
	private DefaultDocumentBox<TimeoutMessage> notifierInbox;
	private TestRegel stopRegel;

	private class DirectExecutor implements Executor {
	     public void execute(Runnable r) {
	         r.run();
	     }
	}

	private class TestRegel implements FilterCondition {

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
		filterInbox = new ExecutorBeobachtbarerEingangskorb<Document>(new DirectExecutor());
		ausgangskorb = new DefaultDocumentBox<MessageCasefile>();
		offeneVorgaenge = new DefaultDocumentBox<MessageCasefile>();
		notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		filterId = FilterId.valueOf(1);
		startRegel = new TestRegel();
		stopRegel = new TestRegel();
	}

	@After
	public void tearDown() throws Exception {
		
	}

	private TimebasedFilterWorker createFilterWorker(TimeoutType timeoutType) {
		return new TimebasedFilterWorker(filterInbox, offeneVorgaenge, notifierInbox, ausgangskorb, new TimebasedFilter(filterId, startRegel, stopRegel, Milliseconds.valueOf(10), timeoutType));
	}

	@Test
	public void testBeginneArbeit() {
		TimebasedFilterWorker sachbearbeiter = createFilterWorker(TimeoutType.SENDE_BEI_TIMEOUT);
		
		assertFalse(sachbearbeiter.isWorking());
		sachbearbeiter.startWorking();
		assertTrue(sachbearbeiter.isWorking());
		sachbearbeiter.stopWorking();
		assertFalse(sachbearbeiter.isWorking());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testHandleNachrichtSendeBeiTimeout() throws UnknownHostException, InterruptedException {
		TimebasedFilterWorker sachbearbeiter = createFilterWorker(TimeoutType.SENDE_BEI_TIMEOUT);
		sachbearbeiter.startWorking();
		
		// Test Startregel trifft nicht zu
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(false);
		
		filterInbox.put(vorgangsmappe);
		
		Assert.assertEquals(0, ausgangskorb.documentCount());

		// Test SENDE_BEI_TIMEOUT
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(true);
		
		filterInbox.put(vorgangsmappe);
		filterInbox.put(notifierInbox.takeDocument());
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		Assert.assertFalse(vorgangsmappe.equals(aeltesterEingang));
		assertEquals(WeiteresVersandVorgehen.VERSENDEN, aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(filterId, aeltesterEingang.getHandledByFilterId());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertTrue(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		// Test cancel bei Stopnachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
				"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		filterInbox.put(vorgangsmappe);
		
		MessageCasefile stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		filterInbox.put(stopVorgangsmappe);
		
		filterInbox.put(notifierInbox.takeDocument());
		
		assertEquals(0, ausgangskorb.documentCount());
		
		// Test andere Nachricht zwischendurch
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		filterInbox.put(vorgangsmappe);
		
		stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		filterInbox.put(stopVorgangsmappe);
		
		assertEquals(0, ausgangskorb.documentCount());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testHandleNachrichtSendeBeiStopRegel() throws UnknownHostException, InterruptedException {
		TimebasedFilterWorker sachbearbeiter = createFilterWorker(TimeoutType.SENDE_BEI_STOP_REGEL);
		sachbearbeiter.startWorking();
		
		// Test Startregel trifft nicht zu
		MessageCasefile vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(false);
		
		filterInbox.put(vorgangsmappe);

		assertEquals(0, ausgangskorb.documentCount());
		
		// Test cancel bei Timeout
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(InetAddress.getLocalHost(), new Date()), new AlarmMessage("start message"));
		
		startRegel.setResult(true);
		
		filterInbox.put(vorgangsmappe);
		filterInbox.put(notifierInbox.takeDocument());

		assertEquals(0, ausgangskorb.documentCount());

		// Test sende bei Stopnachricht
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		filterInbox.put(vorgangsmappe);
		
		MessageCasefile stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(true);
		filterInbox.put(stopVorgangsmappe);
		
		filterInbox.put(notifierInbox.takeDocument());
		
		MessageCasefile aeltesterEingang = ausgangskorb.takeDocument();
		
		assertFalse(vorgangsmappe.equals(aeltesterEingang));
		assertEquals(WeiteresVersandVorgehen.VERSENDEN,
				aeltesterEingang.getWeiteresVersandVorgehen());
		assertEquals(filterId, aeltesterEingang.getHandledByFilterId());
		assertTrue(aeltesterEingang.istAbgeschlossen());
		assertFalse(aeltesterEingang.istAbgeschlossenDurchTimeOut());
		
		assertEquals(0, ausgangskorb.documentCount());
		
		// Test andere Nachricht zwischendurch
		vorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"start message"));
		startRegel.setResult(true);
		stopRegel.setResult(false);
		filterInbox.put(vorgangsmappe);
		
		stopVorgangsmappe = new MessageCasefile(CasefileId.createNew(
				InetAddress.getLocalHost(), new Date()), new AlarmMessage(
						"stop message"));
		startRegel.setResult(false);
		stopRegel.setResult(false);
		filterInbox.put(stopVorgangsmappe);
		
		assertEquals(0, ausgangskorb.documentCount());
	}
}