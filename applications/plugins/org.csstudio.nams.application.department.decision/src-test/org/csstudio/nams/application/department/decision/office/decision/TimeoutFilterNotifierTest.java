package org.csstudio.nams.application.department.decision.office.decision;

import java.net.UnknownHostException;
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
import org.csstudio.nams.service.logging.declaration.LoggerMock;
import org.junit.Before;
import org.junit.Test;

public class TimeoutFilterNotifierTest extends
		AbstractTestObject<TimeoutFilterNotifier> {

	private TimeoutFilterNotifier timeoutFilterNotifier;
	private Inbox<TimeoutMessage> notifierInbox;

//	@Test(timeout=4000)
	public void testIsWorking() throws InterruptedException {
		final TimeoutFilterNotifier timeoutFilterNotifier = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("timeoutFilterNotifier.isWorking()", timeoutFilterNotifier
				.isWorking());
		timeoutFilterNotifier.startWorking();
		Assert.assertTrue("timeoutFilterNotifier.isWorking()", timeoutFilterNotifier
				.isWorking());
		timeoutFilterNotifier.stopWorking();
		Thread.sleep(100);
		Assert.assertFalse("timeoutFilterNotifier.isWorking()", timeoutFilterNotifier
				.isWorking());
	}

	@Test
	public void testTimedNotification() throws UnknownHostException,
			InterruptedException {
		final Inbox<TimeoutMessage> notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		final DefaultDocumentBox<Document> workerInbox = new DefaultDocumentBox<Document>();
		FilterId filterId = FilterId.valueOf(1);
		final Timer timer = new Timer("Notifier");

		final TimeoutMessage timerMessage = getNewTimeoutMessage(filterId);
		final TimeoutFilterNotifier timeoutFilterNotifier = new TimeoutFilterNotifier(
				new DefaultExecutionService(), notifierInbox, timer, new LoggerMock());
		timeoutFilterNotifier.addFilterWorkerInbox(filterId, workerInbox);
		timeoutFilterNotifier.startWorking();

		notifierInbox.put(timerMessage);

		// Warte bis der Bearbeiter fertig sein müsste...
		for (int zaehler = 0; zaehler < 3000; zaehler += 10) {
			if (workerInbox.contains(timerMessage)) {
				break;
			}
			Thread.sleep(10);
		}

		timeoutFilterNotifier.stopWorking();

		final Document ausDemKorb = workerInbox
				.takeDocument();
		Assert.assertNotNull(ausDemKorb);
		Assert.assertEquals(timerMessage, ausDemKorb);
		Assert.assertTrue(timerMessage == ausDemKorb);

		timer.cancel();
	}
	
	@Test
	public void testFilterWorkerInboxes() {
		TimeoutFilterNotifier filterNotifier = getNewInstanceOfClassUnderTest();
		
		FilterId filterId = FilterId.valueOf(2);
		FilterId notContainedFilterId = FilterId.valueOf(888);
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(filterId));
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(notContainedFilterId));
		
		filterNotifier.addFilterWorkerInbox(filterId, new DefaultDocumentBox<Document>());
		Assert.assertTrue(filterNotifier.containsFilterWorkerInbox(filterId));
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(notContainedFilterId));
		
		filterNotifier.removeFilterWorkerInbox(notContainedFilterId);
		Assert.assertTrue(filterNotifier.containsFilterWorkerInbox(filterId));
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(notContainedFilterId));

		filterNotifier.removeFilterWorkerInbox(filterId);
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(filterId));
		Assert.assertFalse(filterNotifier.containsFilterWorkerInbox(notContainedFilterId));
	}
	
	@Test
	public void testMessageHandling() throws InterruptedException, UnknownHostException {
		FilterId filterId = FilterId.valueOf(2);
		DefaultDocumentBox<Document> filterWorkerInbox = new DefaultDocumentBox<Document>();

		FilterId filterId2 = FilterId.valueOf(3);
		DefaultDocumentBox<Document> filterWorkerInbox2 = new DefaultDocumentBox<Document>();
		
		timeoutFilterNotifier.addFilterWorkerInbox(filterId, filterWorkerInbox);
		timeoutFilterNotifier.addFilterWorkerInbox(filterId2, filterWorkerInbox2);
		
		timeoutFilterNotifier.startWorking();
		
		TimeoutMessage timeoutMessage = getNewTimeoutMessage(filterId);
		notifierInbox.put(timeoutMessage);
		Thread.sleep(100);
		Assert.assertTrue(filterWorkerInbox.contains(timeoutMessage));
		Assert.assertEquals(1, filterWorkerInbox.documentCount());
		filterWorkerInbox.takeDocument();
		Assert.assertFalse(filterWorkerInbox2.contains(timeoutMessage));
		Assert.assertEquals(0, filterWorkerInbox2.documentCount());

		FilterId notContainedFilterId = FilterId.valueOf(888);
		notifierInbox.put(getNewTimeoutMessage(notContainedFilterId));
		Thread.sleep(100);
		Assert.assertEquals(0, filterWorkerInbox.documentCount());
		Assert.assertEquals(0, filterWorkerInbox2.documentCount());
	}
	
	@Before
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		
		timeoutFilterNotifier = new TimeoutFilterNotifier(new DefaultExecutionService(), notifierInbox,new Timer("TimeoutNotifier"), new LoggerMock());
	}

	@Override
	protected TimeoutFilterNotifier getNewInstanceOfClassUnderTest() {
		final Inbox<TimeoutMessage> notifierInbox = new DefaultDocumentBox<TimeoutMessage>();
		TimeoutFilterNotifier result = new TimeoutFilterNotifier(new DefaultExecutionService(), notifierInbox,
				new Timer("TimeoutNotifier"), new LoggerMock());
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

	@SuppressWarnings("deprecation")
	private TimeoutMessage getNewTimeoutMessage(FilterId filterId) throws UnknownHostException {
			return TimeoutMessage.valueOf(CasefileId.createNew(), Milliseconds.valueOf(10), filterId);
	}

}
