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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.csstudio.nams.common.decision.Worker;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorbImpl;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter;
import org.csstudio.nams.common.material.regelwerk.WatchDogFilter;
import org.csstudio.nams.common.service.ExecutionService;
import org.csstudio.nams.common.wam.Arbeitsumgebung;
import org.csstudio.nams.service.logging.declaration.ILogger;

/**
 * Repraesentiert die Abteilung Alarm-Entscheidungs-Buero mit Ihrer gesammten
 * Einrichtung. Dieses Buero trifft die Entscheidung, ob eine Nachricht
 * versendet werden soll oder nicht.
 * 
 * Note: Dieses ist die einzige exportierte Klasse dieses Sub-Systems.
 */
@Arbeitsumgebung
public class DecisionDepartment {

	private final Inbox<MessageCasefile> alarmVorgangEingangskorb;
	private final Outbox<MessageCasefile> ausgangskorb;

	private final TimeoutFilterNotifier timeoutFilterNotifier;
	private final ThreadedMessageDispatcher messageDispatcher;
	private Map<FilterId, FilterWorker> _regelwerkKennungenZuSachbearbeitern;
	
	private Timer watchDogTimer;
	private DefaultDocumentBox<TimeoutMessage> terminAssistenzAblagekorb;
	private final ILogger logger;

	/**
	 * Legt ein neues Alarmbuero an. Es wird zugesichert, das nur hier
	 * angeforderte system-externe Komponenten in diesem sub-System verwendet
	 * werden.
	 * 
	 * TODO Logger-Service hinzufuegen/reinreichen um das Logging testbar zu
	 * machen, da dieses wichtig fuer Nachweiszwecke ist.
	 * 
	 * @param filterThreadCount
	 *            TODO
	 * @param historyService
	 */
	public DecisionDepartment(final ExecutionService executionService, List<Filter> regelwerke,
			final Inbox<MessageCasefile> alarmVorgangEingangskorb, final Outbox<MessageCasefile> alarmVorgangAusgangskorb,
			int filterThreadCount, ILogger logger) {

		this.alarmVorgangEingangskorb = alarmVorgangEingangskorb;
		this.ausgangskorb = alarmVorgangAusgangskorb;
		this.logger = logger;
		terminAssistenzAblagekorb = new DefaultDocumentBox<TimeoutMessage>();

		watchDogTimer = new Timer("watchDogTimer");

		this._regelwerkKennungenZuSachbearbeitern = new HashMap<FilterId, FilterWorker>();

		this.timeoutFilterNotifier = new TimeoutFilterNotifier(executionService, terminAssistenzAblagekorb, new Timer(), logger);
		this.messageDispatcher = new ThreadedMessageDispatcher(filterThreadCount, executionService, this.gibAlarmVorgangEingangskorb());
		
		this.updateRegelwerke(regelwerke);
		
		// Starten...
		this.timeoutFilterNotifier.startWorking();
		this.messageDispatcher.startWorking();
	}

	public void updateRegelwerke(List<Filter> regelwerke) {
		logger.logInfoMessage(this, "Updating " + regelwerke.size() + " filter configurations");
		Map<FilterId, FilterWorker> neueSachbearbeiter = new HashMap<FilterId, FilterWorker>();
		
		for (Filter regelwerk : regelwerke) {
			if(_regelwerkKennungenZuSachbearbeitern.containsKey(regelwerk.getFilterId())) {
				FilterWorker vorhandenerSachbearbeiter = _regelwerkKennungenZuSachbearbeitern.remove(regelwerk.getFilterId());
				if(!vorhandenerSachbearbeiter.getFilter().equals(regelwerk)) {
					logger.logInfoMessage(this, "Updating configuration for filter: " + regelwerk);

					// verändert, vorhandenen aktualisieren
					if(vorhandenerSachbearbeiter instanceof DefaultFilterWorker) {
						((DefaultFilterWorker) vorhandenerSachbearbeiter).setRegelwerk((DefaultFilter) regelwerk);
					} else if(vorhandenerSachbearbeiter instanceof TimebasedFilterWorker) {
						((TimebasedFilterWorker) vorhandenerSachbearbeiter).setFilter((TimebasedFilter) regelwerk);
					} else if(vorhandenerSachbearbeiter instanceof WatchDogFilterWorker) {
						((WatchDogFilterWorker) vorhandenerSachbearbeiter).setRegelwerk((WatchDogFilter) regelwerk);
					}
				}
				neueSachbearbeiter.put(vorhandenerSachbearbeiter.getFilter().getFilterId(), vorhandenerSachbearbeiter);
			} else {
				logger.logInfoMessage(this, "New filter: " + regelwerk);
				// neu
				FilterWorker sachbearbeiter = null;
				if (regelwerk instanceof DefaultFilter) {
					ObservableInbox<MessageCasefile> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<MessageCasefile>();
					sachbearbeiter = new DefaultFilterWorker(spezifischerEingangskorb, this.ausgangskorb, (DefaultFilter) regelwerk);
				} else if (regelwerk instanceof TimebasedFilter) {
					ObservableInbox<Document> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<Document>();
					sachbearbeiter = new TimebasedFilterWorker(spezifischerEingangskorb, new DefaultDocumentBox<MessageCasefile>(),
							terminAssistenzAblagekorb, this.ausgangskorb, (TimebasedFilter) regelwerk);
					timeoutFilterNotifier.addFilterWorkerInbox(sachbearbeiter.getFilter().getFilterId(), sachbearbeiter.getInbox());
				} else if (regelwerk instanceof WatchDogFilter) {
					ObservableInbox<MessageCasefile> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<MessageCasefile>();
					sachbearbeiter = new WatchDogFilterWorker(spezifischerEingangskorb, this.ausgangskorb, (WatchDogFilter) regelwerk, watchDogTimer);
				} else {
					throw new RuntimeException("Unhandled subclass of Regelwerk: " + regelwerk.getClass());
				}
				sachbearbeiter.startWorking();
			
				messageDispatcher.addOutbox(sachbearbeiter.getInbox());
				neueSachbearbeiter.put(regelwerk.getFilterId(), sachbearbeiter);
			}
		}
		
		// verbleibende vorhandene Sachbearbeiter löschen
		for (FilterWorker alterSachbearbeiter : _regelwerkKennungenZuSachbearbeitern.values()) {
			logger.logInfoMessage(this, "Delete filter: " + alterSachbearbeiter.getFilter());
			alterSachbearbeiter.stopWorking();
			
			messageDispatcher.removeOutbox(alterSachbearbeiter.getInbox());
			
			if(alterSachbearbeiter instanceof TimebasedFilterWorker) {
				timeoutFilterNotifier.removeFilterWorkerInbox(((TimebasedFilterWorker) alterSachbearbeiter).getId());
			}
		}
		
		_regelwerkKennungenZuSachbearbeitern = neueSachbearbeiter;
		logger.logInfoMessage(this, "Updating " + regelwerke.size() + " filter configurations finished");
	}

	/**
	 * Beendet die Arbeit des Büros. Diese Operation kehrt zurück, wenn alle
	 * Arbeitsgänge erldigt sind und alle offenen Vorgänge in den Ausgangskorb
	 * zum senden gelegt wurden.
	 */
	public void beendeArbeitUndSendeSofortAlleOffeneneVorgaenge() {
		// Terminassistenz beenden...
		this.timeoutFilterNotifier.stopWorking();
		// Sachbearbeiter in den Feierabend schicken...
		for (final Worker sachbearbeiter : _regelwerkKennungenZuSachbearbeitern.values()) {
			sachbearbeiter.stopWorking();
		}
		// Andere Threads zu ende arbeiten lassen
		Thread.yield();
		// Abteilungsleiter in den Feierabend schicken...
		this.messageDispatcher.stopWorking();
	}

	/**
	 * Liefert den Ausgangskorb, in dem die bearbeiteten Vorgaenge abgelegt
	 * werden.
	 */
	public Outbox<MessageCasefile> gibAlarmVorgangAusgangskorb() {
		return this.ausgangskorb;
	}

	/**
	 * Gibt zugriff auf den Eingangskorb für Alarmvorgaenge. Die Mappe wird so
	 * verwendet, wie diese hineingereicht wird. Das Kapitel
	 * "AlarmEntscheidungsbuero" sollte extern nicht veraendert werden.
	 * 
	 * @return Einen Eingangskorb fuer neue Vorgaenge.
	 */
	public Inbox<MessageCasefile> gibAlarmVorgangEingangskorb() {
		return this.alarmVorgangEingangskorb;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenz auf den Abteilungsleiter.
	 */
	ThreadedMessageDispatcher gibAbteilungsleiterFuerTest() {
		return this.messageDispatcher;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenz auf die Terminassistenz.
	 */
	TimeoutFilterNotifier gibAssistenzFuerTest() {
		return this.timeoutFilterNotifier;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenzen auf alle Sachbearbeiter.
	 */
	List<Worker> gibListeDerSachbearbeiterFuerTest() {
		return new ArrayList<Worker>(_regelwerkKennungenZuSachbearbeitern.values());
	}
}
