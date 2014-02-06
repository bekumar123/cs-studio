/* 
 * Copyright (c) 2008 C1 WPS mbH, 
 * HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, 
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER 
 * EXCEPT UNDER THIS DISCLAIMER.
 * C1 WPS HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, 
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE 
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND 
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU 
 * MAY FIND A COPY AT
 * {@link http://www.eclipse.org/org/documents/epl-v10.html}.
 */
package org.csstudio.nams.application.department.decision.office.decision;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;

public class ThreadedMessageDispatcherTest extends
		AbstractTestObject<ThreadedMessageDispatcher> {

	
	
	protected Throwable testFailedError;

	@SuppressWarnings({"unchecked", "deprecation"})
	@Test(timeout = 4000)
	public void testDispatcherThreading() throws Throwable {
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				testFailedError = e;
				Assert.fail("Exception in Thread");
			}
		});
		
		final MessageCasefile messageCasefile = new MessageCasefile(
				CasefileId.createNew(), new AlarmMessage("Test-Nachricht"));

		final Inbox<MessageCasefile> dispatcherInboxMock = EasyMock
				.createMock(Inbox.class);
		EasyMock.expect(dispatcherInboxMock.takeDocument()).andReturn(
				messageCasefile).times(1).andStubAnswer(
				new IAnswer<MessageCasefile>() {
					public MessageCasefile answer() throws Throwable {
						Thread.sleep(Integer.MAX_VALUE);
						Assert.fail();
						return null;
					}
				});
		
		EasyMock.replay(dispatcherInboxMock);

		int threadCount = 4;
		final ThreadedMessageDispatcher threadedMessageDispatcher = new ThreadedMessageDispatcher(threadCount,
				new DefaultExecutionService(), dispatcherInboxMock);

		List<Inbox<Document>> workerInboxes = new ArrayList<Inbox<Document>>(10);
		for(int multipleTimes = 0; multipleTimes < 3; multipleTimes++) {
			for (int i = 0; i < threadCount; i++) {
				// Make sure the threaded dispatcher balances it's loads evenly
				Assert.assertEquals(multipleTimes, threadedMessageDispatcher.getEmptiestDispatcher().getOutboxCount());
				Inbox<Document> workerInboxMock = createWorkerInboxMock(messageCasefile);
				EasyMock.replay(workerInboxMock);
				workerInboxes.add(workerInboxMock);
				threadedMessageDispatcher.addOutbox(workerInboxMock);
			}
		}
		
		this.testFailedError = null;

		threadedMessageDispatcher.startWorking();
		
		// Der Mock simuliert jetzt folgendes:
		// eingangskorb.ablegen(vorgangsmappe);

		// Warte bis der Bearbeiter fertig sein müsste...
		Thread.sleep(1000);

		if (this.testFailedError != null) {
			throw this.testFailedError;
		}

		threadedMessageDispatcher.stopWorking();
		EasyMock.verify(dispatcherInboxMock);
		
		for (Inbox<Document> inbox : workerInboxes) {
			EasyMock.verify(inbox);
		}
	}
	
	private Inbox<Document> createWorkerInboxMock(Document document) throws InterruptedException {
		@SuppressWarnings("unchecked")
		Inbox<Document> result = EasyMock.createMock(Inbox.class);
		result.put(document);
		EasyMock.expectLastCall().once();
		
		return result;
	}
	
	@Test(timeout = 4000)
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void testDispatch() throws Throwable {
		final MessageCasefile messageCasefile = new MessageCasefile(
				CasefileId.createNew(), new AlarmMessage("Test-Nachricht"));
		
		final Inbox<MessageCasefile> dispatcherInboxMock = EasyMock
				.createMock(Inbox.class);
		EasyMock.expect(dispatcherInboxMock.takeDocument()).andReturn(
				messageCasefile).times(1).andStubAnswer(
						new IAnswer<MessageCasefile>() {
							public MessageCasefile answer() throws Throwable {
								Thread.sleep(Integer.MAX_VALUE);
								Assert.fail();
								return null;
							}
						});
		
		EasyMock.replay(dispatcherInboxMock);
		
		Inbox<Document> workerInboxMock = EasyMock.createMock(Inbox.class);
		Capture<Document> capturedPutArguments = new Capture<Document>();
		workerInboxMock.put(EasyMock.<Document> capture(capturedPutArguments));
		EasyMock.expectLastCall().once();
		EasyMock.replay(workerInboxMock);
		
		this.testFailedError = null;
		
		final ThreadedMessageDispatcher threadedMessageDispatcher = new ThreadedMessageDispatcher(1,
				new DefaultExecutionService(), dispatcherInboxMock);
		threadedMessageDispatcher.addOutbox(workerInboxMock);
		
		threadedMessageDispatcher.startWorking();
		
		// Der Mock simuliert jetzt folgendes:
		// eingangskorb.ablegen(vorgangsmappe);
		
		// Warte bis der Bearbeiter fertig sein müsste...
		Thread.sleep(1000);
		
		threadedMessageDispatcher.stopWorking();
		
		if (this.testFailedError != null) {
			throw this.testFailedError;
		}
		EasyMock.verify(dispatcherInboxMock);
		
		EasyMock.verify(workerInboxMock);
		MessageCasefile capturedPutDocument = (MessageCasefile) capturedPutArguments.getValue();
		Assert.assertSame("Vorgangsmappen identisch",
				messageCasefile, capturedPutDocument);
	}

	@Test
	public void testDispatcherStates() throws InterruptedException {
		final ThreadedMessageDispatcher abteilungsleiter = this
				.getNewInstanceOfClassUnderTest();
		Assert.assertFalse("abteilungsleiter.isWorking()", abteilungsleiter
				.isWorking());
		abteilungsleiter.startWorking();
		Assert.assertTrue("abteilungsleiter.isWorking()", abteilungsleiter
				.isWorking());
		abteilungsleiter.stopWorking();
		Thread.sleep(100);
		Assert.assertFalse("abteilungsleiter.isWorking()", abteilungsleiter
				.isWorking());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ThreadedMessageDispatcher getNewInstanceOfClassUnderTest() {
		return new ThreadedMessageDispatcher(1, new DefaultExecutionService(),
				new DefaultDocumentBox<MessageCasefile>());
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ThreadedMessageDispatcher[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new ThreadedMessageDispatcher[] {
				new ThreadedMessageDispatcher(1, new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()),
				new ThreadedMessageDispatcher(1, new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()),
				new ThreadedMessageDispatcher(1, new DefaultExecutionService(),
						new DefaultDocumentBox<MessageCasefile>()) };
	}

}
