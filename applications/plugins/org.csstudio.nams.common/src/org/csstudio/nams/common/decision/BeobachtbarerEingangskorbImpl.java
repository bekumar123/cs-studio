package org.csstudio.nams.common.decision;

public class BeobachtbarerEingangskorbImpl<T extends Document>  extends DefaultDocumentBox<T> implements ObservableInbox<T> {

	private InboxObserver beobachter;

	@Override
	public void setObserver(InboxObserver beobachter) {
		this.beobachter = beobachter;
	}

	public void put(T dokument) throws InterruptedException {
		super.put(dokument);
		
		if(beobachter != null) {
			beobachter.onNewDocument();
		}
	};

}
