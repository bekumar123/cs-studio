package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.Regelwerkskennung;

public class WatchDogRegelwerk implements Regelwerk {

	private Regel regel;
	private Regelwerkskennung regelwerkskennung;
	private Millisekunden delay;

	public WatchDogRegelwerk(Regelwerkskennung regelwerkskennung,
			Regel regel, Millisekunden timeout) {
				this.regelwerkskennung = regelwerkskennung;
				this.regel = regel;
				this.delay = timeout;
	}

	@Override
	public Regelwerkskennung getRegelwerksKennung() {
		return regelwerkskennung;
	}

	public Regel getRegel() {
		return regel;
	}

	public Millisekunden getDelay() {
		return delay;
	}
}
