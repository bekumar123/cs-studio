
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
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.service.ExecutionService;

public class TimebasedFilterNotifier implements Arbeitsfaehig {

	private Map<Integer, TimebasedFilterWorker> timebasedFilterWorkers = new HashMap<Integer, TimebasedFilterWorker>();
	private final InboxReader<TimerMessage> timerMessageInboxReader;
	private final Timer timer;

	private final ExecutionService executionService;

	public TimebasedFilterNotifier(
			final ExecutionService executionService,
			final Inbox<TimerMessage> eingehendeTerminnotizen,
			final Timer timer) {
		this.executionService = executionService;
		this.timer = timer;
		this.timerMessageInboxReader = new InboxReader<TimerMessage>(new TimerMessageHandler(), eingehendeTerminnotizen);
	}
	
	public void addTimebasedFilterWorker(TimebasedFilterWorker worker) {
		timebasedFilterWorkers.put(worker.getId(), worker);
	}
	
	public void removeTimebasedFilterWorker(TimebasedFilterWorker worker) {
		timebasedFilterWorkers.remove(worker.getId());
	}
	
	public boolean containsTimebasedFilterWorker(TimebasedFilterWorker worker) {
		return timebasedFilterWorkers.containsKey(worker.getId());
	}

	public void stopWorking() {
		this.timerMessageInboxReader.stopWorking();
	}

	public void startWorking() {
		this.executionService.executeAsynchronously(
				ThreadTypesOfDecisionDepartment.TERMINASSISTENZ,
				this.timerMessageInboxReader);
		while (!this.timerMessageInboxReader.isCurrentlyRunning()) {
			Thread.yield();
		}
	}

	public boolean isWorking() {
		return this.timerMessageInboxReader.isCurrentlyRunning();
	}

	private class TimerMessageHandler implements DocumentHandler<TimerMessage> {
	
		public void handleDocument(final TimerMessage timerMessage)
				throws InterruptedException {
			final int id = timerMessage.getRecipientWorkerId();
			final Milliseconds wartezeit = timerMessage.getTimeout();
			
			if (!timebasedFilterWorkers.containsKey(id)) {
				throw new RuntimeException("Zu jedem Sachbearbeiter sollte es einen Eingangskorb geben.");
			}
	
			final Inbox<Document> workerInbox = timebasedFilterWorkers.get(id).getInbox();
			
			final TimerTask futureTask = new TimerTask() {
				@Override
				public void run() {
					try {
						workerInbox.put(timerMessage);
					} catch (InterruptedException e) {
						throw new RuntimeException(
								"Ablegen in einen Eingangskorb schlug fehl.", e);
					}
				}
			};
			TimebasedFilterNotifier.this.timer.schedule(futureTask, wartezeit.getMilliseconds());
		}
	}
}
