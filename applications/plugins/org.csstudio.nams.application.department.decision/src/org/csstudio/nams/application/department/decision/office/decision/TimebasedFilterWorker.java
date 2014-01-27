package org.csstudio.nams.application.department.decision.office.decision;

import java.util.Iterator;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.TimebasedRegelwerk.TimeoutType;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;

public class TimebasedFilterWorker implements FilterWorker {

	private TimebasedRegelwerk regelwerk;
	private final ObservableInbox<Document> eingangskorb;
	private final DefaultDocumentBox<MessageCasefile> offeneVorgaenge;
	private final DefaultDocumentBox<TimerMessage> terminAssistenzAblagekorb;
	private final Outbox<MessageCasefile> ausgangskorb;

	private boolean istAmArbeiten;

	public TimebasedFilterWorker(ObservableInbox<Document> eingangskorb,
			DefaultDocumentBox<MessageCasefile> offeneVorgaenge,
			DefaultDocumentBox<TimerMessage> terminAssistenzAblagekorb,
			Outbox<MessageCasefile> ausgangskorb,
			TimebasedRegelwerk regelwerk) {
				this.eingangskorb = eingangskorb;
				this.offeneVorgaenge = offeneVorgaenge;
				this.terminAssistenzAblagekorb = terminAssistenzAblagekorb;
				this.ausgangskorb = ausgangskorb;
				this.regelwerk = regelwerk;
	}

	@Override
	public void stopWorking() {
		istAmArbeiten = false;
		eingangskorb.setObserver(null);
	}

	@Override
	public void startWorking() {
		istAmArbeiten = true;
		
		eingangskorb.setObserver(new InboxObserver() {
			@Override
			public void onNewDocument() {
				handleNeuerEingang();
			}
		});
	}

	@Override
	public boolean isWorking() {
		return istAmArbeiten;
	}

	@Override
	public String toString() {
		return "Sachbearbeiter: " + regelwerk;
	}
	
	private void handleNeuerEingang() {
		Document ablagefaehig;
		try {
			ablagefaehig = eingangskorb.takeDocument();
			if (ablagefaehig instanceof MessageCasefile) {
				MessageCasefile vorgangsmappe = (MessageCasefile) ablagefaehig;
				
				handleOffeneVorgaengeMitNeuerMappe(vorgangsmappe);
				handleNeueMappe(vorgangsmappe);
			} else if (ablagefaehig instanceof TimerMessage) {
				TimerMessage terminnotiz = (TimerMessage) ablagefaehig;
				handleTimeout(terminnotiz);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private MessageCasefile getKopieFuerVersand(MessageCasefile mappe) throws InterruptedException {
		MessageCasefile result = mappe.erstelleKopieFuer(this.toString());
		result.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
		
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
	private void handleTimeout(TimerMessage terminnotiz) throws InterruptedException {
		synchronized (offeneVorgaenge) {
			final Iterator<MessageCasefile> offeneVorgaengeIterator = offeneVorgaenge.iterator();
			while (offeneVorgaengeIterator.hasNext()) {
				final MessageCasefile offenerVorgang = offeneVorgaengeIterator.next();
				if (offenerVorgang.gibMappenkennung().equals(terminnotiz.getCasefileId())) {
					// Entferne Vorgang aus den offenen Vorgängen
					offeneVorgaengeIterator.remove();

					schliesseVorgangDurchTimeoutAb(offenerVorgang, terminnotiz.getCasefileId());
				}
			}
		}
	}

	private void schliesseVorgangDurchTimeoutAb(final MessageCasefile offenerVorgang, CasefileId abgeschlossenDurchKennung)
			throws InterruptedException {
		if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
			// Offener Vorgang ist kein Alarm, da durch Timeout abgebrochen 
		} 
		else if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
			// Offener Vorgang ist ein Alarm, da Timeout abgelaufen
			MessageCasefile kopieFuerVersand = getKopieFuerVersand(offenerVorgang);
			kopieFuerVersand.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
			kopieFuerVersand.pruefungAbgeschlossenDurch(abgeschlossenDurchKennung);
			kopieFuerVersand.abgeschlossenDurchTimeOut();
			
			ausgangskorb.put(kopieFuerVersand);
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
	private void handleOffeneVorgaengeMitNeuerMappe(MessageCasefile aktuellerVorgang) throws InterruptedException {
		synchronized (offeneVorgaenge) {
			// Prüfe bei allen offenen Vorgängen, ob die Vorgangsmappe zu der entsprechenden Stop-Bedingung passt
			final Iterator<MessageCasefile> offeneVorgaengeIterator = offeneVorgaenge.iterator();

			while (offeneVorgaengeIterator.hasNext()) {
				MessageCasefile offenerVorgang = offeneVorgaengeIterator.next();
				boolean trifftRegelZu = regelwerk.getStopRegel().pruefeNachricht(aktuellerVorgang.getAlarmNachricht(),
						offenerVorgang.getAlarmNachricht());

				if (trifftRegelZu) {
					if (regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
						// Offener Vorgang ist ein Alarm, da er bestätigt wurde
						MessageCasefile kopieFuerVersand = getKopieFuerVersand(offenerVorgang);
						kopieFuerVersand.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
						kopieFuerVersand.pruefungAbgeschlossenDurch(aktuellerVorgang.gibMappenkennung());
						ausgangskorb.put(kopieFuerVersand);
					} else if (regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
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
	private void handleNeueMappe(MessageCasefile vorgangsmappe) throws InterruptedException {
		boolean trifftStartRegelZu = regelwerk.getStartRegel().pruefeNachricht(vorgangsmappe.getAlarmNachricht());

		if (trifftStartRegelZu) {
			synchronized (offeneVorgaenge) {
				offeneVorgaenge.put(vorgangsmappe);
			}
			terminAssistenzAblagekorb.put(TimerMessage.valueOf(vorgangsmappe.gibMappenkennung(), regelwerk.getTimeOut(), getId()));
		}
	}

	public int getId() {
		return getRegelwerk().getRegelwerksKennung().getRegelwerksId();
	}

	@Override
	public Regelwerk getRegelwerk() {
		return regelwerk;
	}
	
	public void setRegelwerk(TimebasedRegelwerk regelwerk) {
		// Offene Vorgänge verhalten sich wie bei Eintritt eines Timeouts
		synchronized (offeneVorgaenge) {
			Iterator<MessageCasefile> vorgaengeIterator = offeneVorgaenge.iterator();
			while(vorgaengeIterator.hasNext()) {
				try {
					MessageCasefile offenerVorgang = vorgaengeIterator.next();
					schliesseVorgangDurchTimeoutAb(offenerVorgang, offenerVorgang.gibMappenkennung());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				vorgaengeIterator.remove();
			}
		}
		this.regelwerk = regelwerk;
	}

	@Override
	public Inbox<Document> getInbox() {
		return eingangskorb;
	}
}
