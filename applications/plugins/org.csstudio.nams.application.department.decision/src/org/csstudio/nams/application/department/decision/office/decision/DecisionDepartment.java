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

import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorbImpl;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.WatchDogRegelwerk;
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

	private final TimebasedFilterNotifier _assistenz;
	private final ThreadedMessageDispatcher _abteilungsleiter;
	private Map<Regelwerkskennung, FilterWorker> _regelwerkKennungenZuSachbearbeitern;
	
	private Timer watchDogTimer;
	private DefaultDocumentBox<TimerMessage> terminAssistenzAblagekorb;
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
	public DecisionDepartment(final ExecutionService executionService, List<Regelwerk> regelwerke,
			final Inbox<MessageCasefile> alarmVorgangEingangskorb, final Outbox<MessageCasefile> alarmVorgangAusgangskorb,
			int filterThreadCount, ILogger logger) {

		this.alarmVorgangEingangskorb = alarmVorgangEingangskorb;
		this.ausgangskorb = alarmVorgangAusgangskorb;
		this.logger = logger;
		terminAssistenzAblagekorb = new DefaultDocumentBox<TimerMessage>();

		watchDogTimer = new Timer("watchDogTimer");

		this._regelwerkKennungenZuSachbearbeitern = new HashMap<Regelwerkskennung, FilterWorker>();

		this._assistenz = new TimebasedFilterNotifier(executionService, terminAssistenzAblagekorb, new Timer());
		this._abteilungsleiter = new ThreadedMessageDispatcher(filterThreadCount, executionService, this.gibAlarmVorgangEingangskorb());
		
		this.updateRegelwerke(regelwerke);
		
		// Starten...
		this._assistenz.startWorking();
		this._abteilungsleiter.startWorking();
	}

	public void updateRegelwerke(List<Regelwerk> regelwerke) {
		logger.logInfoMessage(this, "Updating " + regelwerke.size() + " filter configurations");
		Map<Regelwerkskennung, FilterWorker> neueSachbearbeiter = new HashMap<Regelwerkskennung, FilterWorker>();
		
		for (Regelwerk regelwerk : regelwerke) {
			if(_regelwerkKennungenZuSachbearbeitern.containsKey(regelwerk.getRegelwerksKennung())) {
				FilterWorker vorhandenerSachbearbeiter = _regelwerkKennungenZuSachbearbeitern.remove(regelwerk.getRegelwerksKennung());
				if(!vorhandenerSachbearbeiter.getRegelwerk().equals(regelwerk)) {
					logger.logInfoMessage(this, "Updating configuration for filter: " + regelwerk);

					// verändert, vorhandenen aktualisieren
					if(vorhandenerSachbearbeiter instanceof DefaultFilterWorker) {
						((DefaultFilterWorker) vorhandenerSachbearbeiter).setRegelwerk((DefaultRegelwerk) regelwerk);
					} else if(vorhandenerSachbearbeiter instanceof TimebasedFilterWorker) {
						((TimebasedFilterWorker) vorhandenerSachbearbeiter).setRegelwerk((TimebasedRegelwerk) regelwerk);
					} else if(vorhandenerSachbearbeiter instanceof WatchDogFilterWorker) {
						((WatchDogFilterWorker) vorhandenerSachbearbeiter).setRegelwerk((WatchDogRegelwerk) regelwerk);
					}
				}
				neueSachbearbeiter.put(vorhandenerSachbearbeiter.getRegelwerk().getRegelwerksKennung(), vorhandenerSachbearbeiter);
			} else {
				logger.logInfoMessage(this, "New filter: " + regelwerk);
				// neu
				FilterWorker sachbearbeiter = null;
				if (regelwerk instanceof DefaultRegelwerk) {
					ObservableInbox<MessageCasefile> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<MessageCasefile>();
					sachbearbeiter = new DefaultFilterWorker(spezifischerEingangskorb, this.ausgangskorb, (DefaultRegelwerk) regelwerk);
				} else if (regelwerk instanceof TimebasedRegelwerk) {
					ObservableInbox<Document> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<Document>();
					sachbearbeiter = new TimebasedFilterWorker(spezifischerEingangskorb, new DefaultDocumentBox<MessageCasefile>(),
							terminAssistenzAblagekorb, this.ausgangskorb, (TimebasedRegelwerk) regelwerk);
					_assistenz.addTimebasedFilterWorker((TimebasedFilterWorker)sachbearbeiter);
				} else if (regelwerk instanceof WatchDogRegelwerk) {
					ObservableInbox<MessageCasefile> spezifischerEingangskorb = new BeobachtbarerEingangskorbImpl<MessageCasefile>();
					sachbearbeiter = new WatchDogFilterWorker(spezifischerEingangskorb, this.ausgangskorb, (WatchDogRegelwerk) regelwerk, watchDogTimer);
				} else {
					throw new RuntimeException("Unhandled subclass of Regelwerk: " + regelwerk.getClass());
				}
				sachbearbeiter.startWorking();
			
				_abteilungsleiter.addWorkerInbox(sachbearbeiter.getInbox());
				neueSachbearbeiter.put(regelwerk.getRegelwerksKennung(), sachbearbeiter);
			}
		}
		
		// verbleibende vorhandene Sachbearbeiter löschen
		for (FilterWorker alterSachbearbeiter : _regelwerkKennungenZuSachbearbeitern.values()) {
			logger.logInfoMessage(this, "Delete filter: " + alterSachbearbeiter.getRegelwerk());
			alterSachbearbeiter.stopWorking();
			
			_abteilungsleiter.removeWorkerInbox(alterSachbearbeiter.getInbox());
			
			if(alterSachbearbeiter instanceof TimebasedFilterWorker) {
				_assistenz.removeTimebasedFilterWorker((TimebasedFilterWorker)alterSachbearbeiter);
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
		this._assistenz.stopWorking();
		// Sachbearbeiter in den Feierabend schicken...
		for (final Arbeitsfaehig sachbearbeiter : _regelwerkKennungenZuSachbearbeitern.values()) {
			sachbearbeiter.stopWorking();
		}
		// Andere Threads zu ende arbeiten lassen
		Thread.yield();
		// Abteilungsleiter in den Feierabend schicken...
		this._abteilungsleiter.stopWorking();
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
		return this._abteilungsleiter;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenz auf die Terminassistenz.
	 */
	TimebasedFilterNotifier gibAssistenzFuerTest() {
		return this._assistenz;
	}

	/**
	 * Inspector fuer Tests. Liefert die Referenzen auf alle Sachbearbeiter.
	 */
	List<Arbeitsfaehig> gibListeDerSachbearbeiterFuerTest() {
		return new ArrayList<Arbeitsfaehig>(_regelwerkKennungenZuSachbearbeitern.values());
	}
}
