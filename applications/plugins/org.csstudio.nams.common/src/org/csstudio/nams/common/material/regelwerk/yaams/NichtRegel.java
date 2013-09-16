package org.csstudio.nams.common.material.regelwerk.yaams;

import org.csstudio.nams.common.material.AlarmNachricht;

public class NichtRegel implements Regel {
	
	private Regel childRegel;
	
	public NichtRegel(Regel childRegel) {
		this.childRegel = childRegel;
	}

	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht) {
		return !childRegel.pruefeNachricht(nachricht);
	}

	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht, AlarmNachricht vergleichsNachricht) {
		return !childRegel.pruefeNachricht(nachricht, vergleichsNachricht);
	}

}
