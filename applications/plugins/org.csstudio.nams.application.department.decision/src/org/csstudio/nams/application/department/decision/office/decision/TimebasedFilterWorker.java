package org.csstudio.nams.application.department.decision.office.decision;

import java.util.Iterator;

import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.Clipboard;
import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter;
import org.csstudio.nams.common.material.regelwerk.TimebasedFilter.TimeoutType;

public class TimebasedFilterWorker implements FilterWorker {

	private TimebasedFilter filter;
	private final ObservableInbox<Document> inbox;
	private final Clipboard<MessageCasefile> openCasefiles;
	private final Outbox<TimeoutMessage> timeoutNotifierBox;
	private final Outbox<MessageCasefile> outbox;

	private boolean isWorking;

	public TimebasedFilterWorker(ObservableInbox<Document> inbox,
			Clipboard<MessageCasefile> openCasefiles,
			Outbox<TimeoutMessage> timeoutNotifierBox,
			Outbox<MessageCasefile> outbox,
			TimebasedFilter filter) {
				this.inbox = inbox;
				this.openCasefiles = openCasefiles;
				this.timeoutNotifierBox = timeoutNotifierBox;
				this.outbox = outbox;
				this.filter = filter;
	}

	@Override
	public void stopWorking() {
		isWorking = false;
		inbox.setObserver(null);
	}

	@Override
	public void startWorking() {
		isWorking = true;
		
		inbox.setObserver(new InboxObserver() {
			@Override
			public void onNewDocument() {
				handleNewMessage();
			}
		});
	}

	@Override
	public boolean isWorking() {
		return isWorking;
	}

	@Override
	public String toString() {
		return "Sachbearbeiter: " + filter;
	}
	
	public FilterId getId() {
		return getFilter().getFilterId();
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	public void setFilter(TimebasedFilter regelwerk) {
		// Offene Vorgänge verhalten sich wie bei Eintritt eines Timeouts
		synchronized (openCasefiles) {
			Iterator<MessageCasefile> vorgaengeIterator = openCasefiles.iterator();
			while(vorgaengeIterator.hasNext()) {
				try {
					MessageCasefile offenerVorgang = vorgaengeIterator.next();
					closeCaseWithTimeout(offenerVorgang, offenerVorgang.getCasefileId());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				vorgaengeIterator.remove();
			}
		}
		this.filter = regelwerk;
	}

	@Override
	public Inbox<Document> getInbox() {
		return inbox;
	}

	private void handleNewMessage() {
		Document ablagefaehig;
		try {
			ablagefaehig = inbox.takeDocument();
			if (ablagefaehig instanceof MessageCasefile) {
				MessageCasefile vorgangsmappe = (MessageCasefile) ablagefaehig;
				
				handleOpenCaseFilesWithNewMessage(vorgangsmappe);
				handleNewMessage(vorgangsmappe);
			} else if (ablagefaehig instanceof TimeoutMessage) {
				TimeoutMessage terminnotiz = (TimeoutMessage) ablagefaehig;
				handleTimeout(terminnotiz);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private MessageCasefile createMessageCopyToSend(MessageCasefile mappe) throws InterruptedException {
		MessageCasefile result = mappe.erstelleKopieFuer(this.toString());
		result.setHandledWithFilter(filter.getFilterId());
		
		return result;
	}

	/**
	 * Zu einer eingehenden Timer-Benachrichtigung wird der zugehoerige offene
	 * Vorgang gesucht und je nach Konfiguration des Filters ein Alarm
	 * ausgeloest oder kein Alarm ausgeloest. Der zu dem Timer gehoerende
	 * Vorgang wird in jedem Fall abgeschlossen.
	 * 
	 * Der zum Timer gehoerende offene Vorgang kann bereits vorher abgeschlossen
	 * worden sein, in diesem Fall hat die Timer-Benachrichtigung keine
	 * Auswirkungen.
	 * 
	 * @throws InterruptedException
	 */
	private void handleTimeout(TimeoutMessage timeoutMessage) throws InterruptedException {
		synchronized (openCasefiles) {
			final Iterator<MessageCasefile> offeneVorgaengeIterator = openCasefiles.iterator();
			while (offeneVorgaengeIterator.hasNext()) {
				final MessageCasefile offenerVorgang = offeneVorgaengeIterator.next();
				if (offenerVorgang.getCasefileId().equals(timeoutMessage.getCasefileId())) {
					// Entferne Vorgang aus den offenen Vorgängen
					offeneVorgaengeIterator.remove();

					closeCaseWithTimeout(offenerVorgang, timeoutMessage.getCasefileId());
				}
			}
		}
	}

	private void closeCaseWithTimeout(final MessageCasefile offenerVorgang, CasefileId abgeschlossenDurchKennung)
			throws InterruptedException {
		if(filter.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
			// Offener Vorgang ist kein Alarm, da durch Timeout abgebrochen 
		} 
		else if(filter.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
			// Offener Vorgang ist ein Alarm, da Timeout abgelaufen
			MessageCasefile kopieFuerVersand = createMessageCopyToSend(offenerVorgang);
			kopieFuerVersand.pruefungAbgeschlossenDurch(abgeschlossenDurchKennung);
			kopieFuerVersand.abgeschlossenDurchTimeOut();
			
			outbox.put(kopieFuerVersand);
		}

	}

	
	/**
	 * Eine eingegangene Alarm-Nachricht wird als moegliche Stop-Bedingung für
	 * alle offenen Vorgaenge geprueft. Falls die Stop-Bedingung fuer einen
	 * offenen Vorgang erfuellt wird, ist dieser offene Vorgang abgeschlossen.
	 * Je nach Konfiguration bedeutet das entweder, dass ein Alarm ausgeloest
	 * wird oder das kein Alarm ausgeloest wird.
	 * 
	 * @throws InterruptedException
	 */
	private void handleOpenCaseFilesWithNewMessage(MessageCasefile aktuellerVorgang) throws InterruptedException {
		synchronized (openCasefiles) {
			// Prüfe bei allen offenen Vorgängen, ob die Vorgangsmappe zu der entsprechenden Stop-Bedingung passt
			final Iterator<MessageCasefile> offeneVorgaengeIterator = openCasefiles.iterator();

			while (offeneVorgaengeIterator.hasNext()) {
				MessageCasefile offenerVorgang = offeneVorgaengeIterator.next();
				boolean trifftRegelZu = filter.getStopRegel().pruefeNachricht(aktuellerVorgang.getAlarmMessage(),
						offenerVorgang.getAlarmMessage());

				if (trifftRegelZu) {
					if (filter.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
						// Offener Vorgang ist ein Alarm, da er bestätigt wurde
						MessageCasefile kopieFuerVersand = createMessageCopyToSend(offenerVorgang);
						kopieFuerVersand.pruefungAbgeschlossenDurch(aktuellerVorgang.getCasefileId());
						outbox.put(kopieFuerVersand);
					} else if (filter.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
						// Offener Vorgang ist kein Alarm, da er abgebrochen
						// wurde
					}

					// Entferne Vorgang aus den offenen Vorgängen
					offeneVorgaengeIterator.remove();

				}
			}
		}
	}

	/**
	 * Erstmaliges Pruefen einer eingehenden Alarm-Nachricht. Hier wird gegen
	 * die Start-Filterbedingungen geprueft. Wenn die Bedingungen erfüllt sind,
	 * wird die Nachricht für spätere Prüfungen aufbewahrt und ein Timeout
	 * gestartet.
	 * 
	 * @throws InterruptedException
	 */
	private void handleNewMessage(MessageCasefile vorgangsmappe) throws InterruptedException {
		boolean trifftStartRegelZu = filter.getStartRegel().pruefeNachricht(vorgangsmappe.getAlarmMessage());

		if (trifftStartRegelZu) {
			synchronized (openCasefiles) {
				openCasefiles.put(vorgangsmappe);
			}
			timeoutNotifierBox.put(TimeoutMessage.valueOf(vorgangsmappe.getCasefileId(), filter.getTimeOut(), getId()));
		}
	}
}
