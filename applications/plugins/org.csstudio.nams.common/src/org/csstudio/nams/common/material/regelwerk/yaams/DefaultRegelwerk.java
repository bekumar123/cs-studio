package org.csstudio.nams.common.material.regelwerk.yaams;

import org.csstudio.nams.common.material.Regelwerkskennung;

public class DefaultRegelwerk implements NewRegelwerk {

	private final Regel regel;
	private final Regelwerkskennung regelwerkskennung;

	public DefaultRegelwerk(Regelwerkskennung regelwerkskennung, Regel regel) {
		this.regelwerkskennung = regelwerkskennung;
		this.regel = regel;
	}
	
	public Regel getRegel() {
		return regel;
	}

	@Override
	public Regelwerkskennung getRegelwerksKennung() {
		return regelwerkskennung;
	}
}
