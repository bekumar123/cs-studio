
/*
 * Copyright (c) C1 WPS mbH, HAMBURG, GERMANY. All Rights Reserved.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.nams.application.department.decision.ThreadTypesOfDecisionDepartment;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Worker;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.service.logging.declaration.ILogger;

public class TimeoutFilterNotifier implements Worker {

	private Map<FilterId, Inbox<Document>> filterWorkerInboxes = new HashMap<FilterId, Inbox<Document>>();
	protected InboxReader<TimeoutMessage> timerMessageInboxReader;
	private final Timer timer;

	private final ExecutionService executionService;
	private final ILogger logger;

	public TimeoutFilterNotifier(
			final ExecutionService executionService,
			final Inbox<TimeoutMessage> timerMessageInbox,
			final Timer timer,
			final ILogger logger) {
		this.executionService = executionService;
		this.timer = timer;
		this.logger = logger;
		this.timerMessageInboxReader = new InboxReader<TimeoutMessage>(new TimerMessageHandler(), timerMessageInbox);
	}
	
	public void addFilterWorkerInbox(FilterId filterId, Inbox<Document> filterWorkerInbox) {
		filterWorkerInboxes.put(filterId, filterWorkerInbox);
	}
	
	public void removeFilterWorkerInbox(FilterId filterId) {
		filterWorkerInboxes.remove(filterId);
	}
	
	public boolean containsFilterWorkerInbox(FilterId filterId) {
		return filterWorkerInboxes.containsKey(filterId);
	}

	public void stopWorking() {
		this.timerMessageInboxReader.stopWorking();
	}

	public void startWorking() {
		this.executionService.executeAsynchronously(ThreadTypesOfDecisionDepartment.TIMEOUT_FILTER_NOTIFIER, this.timerMessageInboxReader);
		while (!this.timerMessageInboxReader.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public boolean isWorking() {
		return this.timerMessageInboxReader.isCurrentlyRunning();
	}

	private class TimerMessageHandler implements DocumentHandler<TimeoutMessage> {
	
		public void handleDocument(final TimeoutMessage timerMessage)
				throws InterruptedException {
			final FilterId id = timerMessage.getRecipientWorkerId();
			final Milliseconds wartezeit = timerMessage.getTimeout();
			
			if (!filterWorkerInboxes.containsKey(id)) {
				logger.logWarningMessage(this, "Could not find a FilterWorker inbox for id: " + id + ".");
			} else {
				final Inbox<Document> workerInbox = filterWorkerInboxes.get(id);

				final TimerTask futureTask = new TimerTask() {
					@Override
					public void run() {
						try {
							workerInbox.put(timerMessage);
						} catch (InterruptedException e) {
							logger.logWarningMessage(this, "Could not put timer message into inbox for worker " + id, e);
						}
					}
				};
				
				TimeoutFilterNotifier.this.timer.schedule(futureTask, wartezeit.getMilliseconds());
			}
		}
	}
}
