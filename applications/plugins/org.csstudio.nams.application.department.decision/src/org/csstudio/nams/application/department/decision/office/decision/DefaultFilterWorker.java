package org.csstudio.nams.application.department.decision.office.decision;

import org.csstudio.nams.common.decision.Document;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.InboxObserver;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.decision.ObservableInbox;
import org.csstudio.nams.common.decision.Outbox;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.Filter;

public class DefaultFilterWorker implements FilterWorker {

	private DefaultFilter regelwerk;
	private final ObservableInbox<MessageCasefile> eingangskorb;
	private final Outbox<MessageCasefile> ausgangskorb;
	
	private boolean istAmArbeiten;
	
	public DefaultFilterWorker(			
			final ObservableInbox<MessageCasefile> eingangskorb,
			final Outbox<MessageCasefile> ausgangskorb,
			final DefaultFilter regelwerk) 
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
			boolean trifftRegelZu = regelwerk.getRegel().pruefeNachricht(vorgangsMappe.getAlarmMessage());
			
			if(trifftRegelZu) {
				MessageCasefile erstelleKopieFuer = vorgangsMappe.erstelleKopieFuer(this.toString());
				erstelleKopieFuer.setHandledWithFilter(regelwerk.getFilterId());
				erstelleKopieFuer.pruefungAbgeschlossenDurch(vorgangsMappe.getCasefileId());
				ausgangskorb.put(erstelleKopieFuer);
			}
		} 
		catch (InterruptedException e) {
			// TODO: Log properly
			e.printStackTrace();
		}
	}

	@Override
	public Filter getFilter() {
		return regelwerk;
	}

	public void setRegelwerk(DefaultFilter regelwerk) {
		this.regelwerk = regelwerk;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Inbox<Document> getInbox() {
		return (Inbox) eingangskorb;
	}
}
