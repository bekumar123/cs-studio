package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.decision.Arbeitsfaehig;
import org.csstudio.nams.common.decision.Ausgangskorb;
import org.csstudio.nams.common.decision.BeobachtbarerEingangskorb;
import org.csstudio.nams.common.decision.EingangskorbBeobachter;
import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.common.material.regelwerk.yaams.DefaultRegelwerk;

public class DefaultSachbearbeiter implements Arbeitsfaehig {

	private final DefaultRegelwerk regelwerk;
	private final BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb;
	private final Ausgangskorb<Vorgangsmappe> ausgangskorb;
	
	private boolean istAmArbeiten;
	
	public DefaultSachbearbeiter(			
			final BeobachtbarerEingangskorb<Vorgangsmappe> eingangskorb,
			final Ausgangskorb<Vorgangsmappe> ausgangskorb,
			final DefaultRegelwerk regelwerk) 
	{
		this.eingangskorb = eingangskorb;
		this.ausgangskorb = ausgangskorb;
		this.regelwerk = regelwerk;
		
		this.istAmArbeiten = false;
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
	
	private void handleNeuerEingang() {
		try {
			Vorgangsmappe vorgangsMappe = eingangskorb.entnehmeAeltestenEingang();
			vorgangsMappe.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
			boolean trifftRegelZu = regelwerk.getRegel().pruefeNachricht(vorgangsMappe.getAlarmNachricht());
			
			if(trifftRegelZu) {
				vorgangsMappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
			} else {
				vorgangsMappe.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.NICHT_VERSENDEN);
			}
			vorgangsMappe.pruefungAbgeschlossenDurch(vorgangsMappe.gibMappenkennung());
			ausgangskorb.ablegen(vorgangsMappe);
		} 
		catch (InterruptedException e) {
			// TODO: Log properly
			e.printStackTrace();
		}
	}

}
