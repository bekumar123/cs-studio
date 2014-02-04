package org.csstudio.nams.application.department.decision.office.decision;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.junit.Test;

public class TimeoutFilterNotifierTest extends
		AbstractTestObject<TimeoutFilterNotifier> {

	@Test
	public void testArbeit() throws InterruptedException {
		final TimeoutFilterNotifier terminassistenz = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("terminassistenz.isWorking()", terminassistenz
				.isWorking());
		terminassistenz.startWorking();
		Assert.assertTrue("terminassistenz.isWorking()", terminassistenz
				.isWorking());
		terminassistenz.stopWorking();
		Thread.sleep(100);
		Assert.assertFalse("terminassistenz.isWorking()", terminassistenz
				.isWorking());
	}

	@Test
	public void testAssistenz() throws UnknownHostException,
			InterruptedException {
		final Inbox<TimeoutMessage> notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		final DefaultDocumentBox<Document> workerInbox = new DefaultDocumentBox<Document>();
		FilterId filterId = FilterId.valueOf(1);
		final Timer timer = new Timer("Notifier");

		final TimeoutMessage timerMessage = TimeoutMessage.valueOf(
				CasefileId.valueOf(InetAddress
						.getByAddress(new byte[] { 127, 0, 0, 1 }),
						new Date(42)), Milliseconds.valueOf(42), filterId);

		final TimeoutFilterNotifier assistenz = new TimeoutFilterNotifier(
				new DefaultExecutionService(), notifierInbox, timer);
		assistenz.addFilterWorkerInbox(filterId, workerInbox);
		assistenz.startWorking();

		notifierInbox.put(timerMessage);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			if (workerInbox.contains(timerMessage)) {
				break;
			}
			Thread.sleep(10);
		}

		assistenz.stopWorking();

		final Document ausDemKorb = workerInbox
				.takeDocument();
		Assert.assertNotNull(ausDemKorb);
		Assert.assertEquals(timerMessage, ausDemKorb);
		Assert.assertTrue(timerMessage == ausDemKorb);

		timer.cancel();
	}

	@Override
	protected TimeoutFilterNotifier getNewInstanceOfClassUnderTest() {
		final Inbox<TimeoutMessage> notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		TimeoutFilterNotifier result = new TimeoutFilterNotifier(new DefaultExecutionService(), notifierInbox,
				new Timer("TerminAssistenz"));
		result.addFilterWorkerInbox(FilterId.valueOf(1), new DefaultDocumentBox<Document>());
		
		return result;
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected TimeoutFilterNotifier[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new TimeoutFilterNotifier[] { this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest(),
				this.getNewInstanceOfClassUnderTest() };
	}

}
