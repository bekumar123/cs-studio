package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.material.regelwerk.DefaultRegelwerk;
import org.csstudio.nams.common.material.regelwerk.Regelwerk;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;

public class DefaultFilterWorker implements FilterWorker {

	private DefaultRegelwerk regelwerk;
	private final ObservableInbox<MessageCasefile> eingangskorb;
	private final Outbox<MessageCasefile> ausgangskorb;
	
	private boolean istAmArbeiten;
	
	public DefaultFilterWorker(			
			final ObservableInbox<MessageCasefile> eingangskorb,
			final Outbox<MessageCasefile> ausgangskorb,
			final DefaultRegelwerk regelwerk) 
	{
		this.eingangskorb = eingangskorb;
		this.ausgangskorb = ausgangskorb;
		this.regelwerk = regelwerk;
		
		this.istAmArbeiten = false;
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
	
	private void handleNeuerEingang() {
		try {
			MessageCasefile vorgangsMappe = eingangskorb.takeDocument();
			boolean trifftRegelZu = regelwerk.getRegel().pruefeNachricht(vorgangsMappe.getAlarmNachricht());
			
			if(trifftRegelZu) {
				MessageCasefile erstelleKopieFuer = vorgangsMappe.erstelleKopieFuer(this.toString());
				erstelleKopieFuer.setBearbeitetMitRegelWerk(regelwerk.getRegelwerksKennung());
				erstelleKopieFuer.setWeiteresVersandVorgehen(WeiteresVersandVorgehen.VERSENDEN);
				erstelleKopieFuer.pruefungAbgeschlossenDurch(vorgangsMappe.gibMappenkennung());
				ausgangskorb.put(erstelleKopieFuer);
			}
		} 
		catch (InterruptedException e) {
			// TODO: Log properly
			e.printStackTrace();
		}
	}

	@Override
	public Regelwerk getRegelwerk() {
		return regelwerk;
	}

	public void setRegelwerk(DefaultRegelwerk regelwerk) {
		this.regelwerk = regelwerk;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Inbox<Document> getInbox() {
		return (Inbox) eingangskorb;
	}
}
