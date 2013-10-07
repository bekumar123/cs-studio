package org.csstudio.nams.common.material.regelwerk.yaams;

import org.csstudio.nams.common.material.Regelwerkskennung;

public class WatchDogRegelwerk implements NewRegelwerk {

	private Regel regel;
	private Regelwerkskennung regelwerkskennung;
	private int delay;

	public WatchDogRegelwerk(Regelwerkskennung regelwerkskennung,
			Regel regel, int timeout) {
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

	public long getDelay() {
		return delay;
	}
}
