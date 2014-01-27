
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

import java.util.ArrayList;
import java.util.List;

import org.csstudio.nams.application.department.decision.ThreadTypesOfDecisionDepartment;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.wam.Automat;

/**
 * Der Abteilungsleiter ist verantwortlich für die Bearbeitung von Nachrichten.
 */
@Automat
class ThreadedMessageDispatcher implements DocumentHandler<MessageCasefile>,
		Arbeitsfaehig {
	private final InboxReader<MessageCasefile> inboxListener;
	private final ExecutionService executionService;
	private List<MessageDispatcher> messageDispatchers;
	private List<Inbox<MessageCasefile>> dispatcherInboxes;
	private List<InboxReader<MessageCasefile>> inboxListeners;

	/**
	 * Legt den Abteilungsleiter an, der die Alarmvorgänge an seine
	 * Sachbearbeiter verteilt.
	 */
	public ThreadedMessageDispatcher(final int threadCount, final ExecutionService executionService, final Inbox<MessageCasefile> inbox) {
		
		this.messageDispatchers = new ArrayList<ThreadedMessageDispatcher.MessageDispatcher>(threadCount);
		this.dispatcherInboxes = new ArrayList<Inbox<MessageCasefile>>(threadCount);
		this.inboxListeners = new ArrayList<InboxReader<MessageCasefile>>(threadCount);
		
		for(int index = 0; index < threadCount; index++) {
			DefaultDocumentBox<MessageCasefile> ablagekorb = new DefaultDocumentBox<MessageCasefile>();
			MessageDispatcher messageDispatcher = new MessageDispatcher();
			InboxReader<MessageCasefile> inboxListener = new InboxReader<MessageCasefile>(messageDispatcher, ablagekorb);
			
			this.messageDispatchers.add(messageDispatcher);
			this.dispatcherInboxes.add(ablagekorb);
			this.inboxListeners.add(inboxListener);
		}
		
		this.executionService = executionService;
		this.inboxListener = new InboxReader<MessageCasefile>(this, inbox);
	}
	
	public void addWorkerInbox(Inbox<Document> workerInbox) {
		assert !containsWorkerInbox(workerInbox) : "!containsWorkerInbox(workerInbox)";
		
		MessageDispatcher dispatcher = getEmptiestDispatcher();
		dispatcher.addOutbox(workerInbox);
	}
	
	public void removeWorkerInbox(Inbox<Document> workerInbox) {
		for (MessageDispatcher dispatcher : messageDispatchers) {
			dispatcher.removeOutbox(workerInbox);
		}
	}
	
	public boolean containsWorkerInbox(Inbox<Document> workerInbox) {
		for (MessageDispatcher dispatcher : messageDispatchers) {
			if(dispatcher.containsOutbox(workerInbox)) {
				return true;
			}
		}
		
		return false;
	}
	
	private MessageDispatcher getEmptiestDispatcher() {
		MessageDispatcher result = null;
		for (MessageDispatcher dispatcher : messageDispatchers) {
			if(result == null) {
				result = dispatcher;
			} else if (dispatcher.getOutboxCount() < result.getOutboxCount()) {
				result = dispatcher;
			}
		}
		
		return result;
	}

	/**
	 * Delegiert Vorgaende an die {@link FilterWorker}.
	 */
	public void handleDocument(final MessageCasefile casefile) {
		
		for (Inbox<MessageCasefile> dispatcherInbox : dispatcherInboxes) {
			while (dispatcherInbox.documentCount() > 100) {
				try {
					Thread.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				dispatcherInbox.put(casefile);
			} catch (InterruptedException e) {
				// Ignore, application is shutting down
			}
		}
	}

	/**
	 * Beendet die Arbeit.
	 */
	public void stopWorking() {
		this.inboxListener.stopWorking();
		for (InboxReader<MessageCasefile> inboxListener : inboxListeners) {
			inboxListener.stopWorking();
		}
	}

	/**
	 * Beginnt mit der Arbeit, das auslesen neuer Nachrichten und das delegieren
	 * der Aufgaben etc..
	 */
	public void startWorking() {
		for (InboxReader<MessageCasefile> inboxListener : inboxListeners) {
			executionService.executeAsynchronously(ThreadTypesOfDecisionDepartment.FILTER_WORKER, inboxListener);
		}
		this.executionService.executeAsynchronously(ThreadTypesOfDecisionDepartment.THREADED_MESSAGE_DISPATCHER, this.inboxListener);

		while (!this.inboxListener.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public boolean isWorking() {
		return this.inboxListener.isCurrentlyRunning();
	}
	
	private class MessageDispatcher implements DocumentHandler<MessageCasefile> {

		private List<Inbox<Document>> outboxes;
		
		public MessageDispatcher() {
			this.outboxes = new ArrayList<Inbox<Document>>();
		}
		
		public boolean containsOutbox(Inbox<Document> outbox) {
			return outboxes.contains(outbox);
		}
		
		public void addOutbox(Inbox<Document> outbox) {
			outboxes.add(outbox);
		}
		
		public void removeOutbox(Inbox<Document> outbox) {
			outboxes.remove(outbox);
		}
		
		public int getOutboxCount() {
			return outboxes.size();
		}
		
		@Override
		public void handleDocument(MessageCasefile message) throws InterruptedException {
			for (Inbox<Document> outbox : outboxes) {
				try {
					outbox.put(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
