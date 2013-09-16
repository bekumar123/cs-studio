package org.csstudio.nams.application.department.decision.office.decision;

import java.util.Iterator;

import org.csstudio.nams.common.decision.Ablagefaehig;
import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.EingangskorbBeobachter;
import org.csstudio.nams.common.decision.StandardAblagekorb;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk;
import org.csstudio.nams.common.material.regelwerk.yaams.TimebasedRegelwerk.TimeoutType;

public class TimebasedSachbearbeiter implements Arbeitsfaehig {

	private final String name;
	private final BeobachtbarerEingangskorb<Ablagefaehig> eingangskorb;
	private final StandardAblagekorb<Vorgangsmappe> offeneVorgaenge;
	private final StandardAblagekorb<Terminnotiz> terminAssistenzAblagekorb;
	private final Ausgangskorb<Vorgangsmappe> ausgangskorb;
	private final TimebasedRegelwerk regelwerk;

	private boolean istAmArbeiten;

	public TimebasedSachbearbeiter(String name,
			BeobachtbarerEingangskorb<Ablagefaehig> eingangskorb,
			StandardAblagekorb<Vorgangsmappe> offeneVorgaenge,
			StandardAblagekorb<Terminnotiz> terminAssistenzAblagekorb,
			Ausgangskorb<Vorgangsmappe> ausgangskorb,
			TimebasedRegelwerk regelwerk) {
				this.name = name;
				this.eingangskorb = eingangskorb;
				this.offeneVorgaenge = offeneVorgaenge;
				this.terminAssistenzAblagekorb = terminAssistenzAblagekorb;
				this.ausgangskorb = ausgangskorb;
				this.regelwerk = regelwerk;
	}

	@Override
	public void beendeArbeit() {
		istAmArbeiten = false;
		eingangskorb.setBeobachter(null);
	}

	@Override
	public void beginneArbeit() {
		istAmArbeiten = true;
		
		eingangskorb.setBeobachter(new EingangskorbBeobachter() {
			@Override
			public void neuerEingang() {
				handleNeuerEingang();
			}
		});
	}

	@Override
	public boolean istAmArbeiten() {
		return istAmArbeiten;
	}

	public String gibName() {
		return name;
	}

	@Override
	public String toString() {
		return "Sachbearbeiter: " + regelwerk;
	}
	
	private void handleNeuerEingang() {
		Ablagefaehig ablagefaehig;
		try {
			ablagefaehig = eingangskorb.entnehmeAeltestenEingang();
			if (ablagefaehig instanceof Vorgangsmappe) {
				Vorgangsmappe vorgangsmappe = (Vorgangsmappe) ablagefaehig;
				vorgangsmappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
				
				handleOffeneVorgaengeMitNeuerMappe(vorgangsmappe);
				handleNeueMappe(vorgangsmappe);
			} else if (ablagefaehig instanceof Terminnotiz) {
				Terminnotiz terminnotiz = (Terminnotiz) ablagefaehig;
				handleTimeout(terminnotiz);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	private void handleTimeout(Terminnotiz terminnotiz) throws InterruptedException {
		final Iterator<Vorgangsmappe> offeneVorgaengeIterator = offeneVorgaenge.iterator();
		while (offeneVorgaengeIterator.hasNext()) {
			final Vorgangsmappe offenerVorgang = offeneVorgaengeIterator.next();
			if (offenerVorgang.gibMappenkennung().equals(terminnotiz.gibVorgangsmappenkennung())) {
				if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
					// Offener Vorgang ist kein Alarm, da durch Timeout abgebrochen 
					offenerVorgang.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.NICHT_VERSENDEN);
				} 
				else if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
					// Offener Vorgang ist ein Alarm, da Timeout abgelaufen
					offenerVorgang.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
				}

				offenerVorgang.pruefungAbgeschlossenDurch(terminnotiz.gibVorgangsmappenkennung());
				offenerVorgang.abgeschlossenDurchTimeOut();
				
				// Entferne Vorgang aus den offenen Vorgängen
				offeneVorgaengeIterator.remove();

				ausgangskorb.ablegen(offenerVorgang);
			}
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
	private void handleOffeneVorgaengeMitNeuerMappe(Vorgangsmappe aktuellerVorgang) throws InterruptedException {
		// Prüfe bei allen offenen Vorgängen, ob die Vorgangsmappe zu der entsprechenden Stop-Bedingung passt
		final Iterator<Vorgangsmappe> offeneVorgaengeIterator = offeneVorgaenge.iterator();

		while(offeneVorgaengeIterator.hasNext()) {
			Vorgangsmappe offenerVorgang = offeneVorgaengeIterator.next();
			boolean trifftRegelZu = regelwerk.getStopRegel().pruefeNachricht(aktuellerVorgang.getAlarmNachricht(), offenerVorgang.getAlarmNachricht());
			
			if(trifftRegelZu) {
				if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_STOP_REGEL) {
					// Offener Vorgang ist ein Alarm, da er bestätigt wurde
					offenerVorgang.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
				} else if(regelwerk.getTimeoutType() == TimeoutType.SENDE_BEI_TIMEOUT) {
					// Offener Vorgang ist kein Alarm, da er abgebrochen wurde
					offenerVorgang.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.NICHT_VERSENDEN);
				}
				
				offenerVorgang.pruefungAbgeschlossenDurch(aktuellerVorgang.gibMappenkennung());
				// Entferne Vorgang aus den offenen Vorgängen
				offeneVorgaengeIterator.remove();
				
				ausgangskorb.ablegen(offenerVorgang);
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
	private void handleNeueMappe(Vorgangsmappe vorgangsmappe) throws InterruptedException {
		boolean trifftRegelZu = regelwerk.getStartRegel().pruefeNachricht(vorgangsmappe.getAlarmNachricht());

		if (trifftRegelZu) {
			offeneVorgaenge.ablegen(vorgangsmappe);
			terminAssistenzAblagekorb.ablegen(Terminnotiz.valueOf(vorgangsmappe.gibMappenkennung(), regelwerk.getTimeOut(), name));
		} else {
			vorgangsmappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.NICHT_VERSENDEN);
			vorgangsmappe.pruefungAbgeschlossenDurch(vorgangsmappe.gibMappenkennung());
			ausgangskorb.ablegen(vorgangsmappe);
		}
	}
}
