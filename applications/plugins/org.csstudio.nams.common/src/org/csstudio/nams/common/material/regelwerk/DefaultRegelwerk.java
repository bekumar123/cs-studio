package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.Regelwerkskennung;

public class DefaultRegelwerk implements Regelwerk {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regel == null) ? 0 : regel.hashCode());
		result = prime * result + ((regelwerkskennung == null) ? 0 : regelwerkskennung.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultRegelwerk other = (DefaultRegelwerk) obj;
		if (regel == null) {
			if (other.regel != null)
				return false;
		} else if (!regel.equals(other.regel))
			return false;
		if (regelwerkskennung == null) {
			if (other.regelwerkskennung != null)
				return false;
		} else if (!regelwerkskennung.equals(other.regelwerkskennung))
			return false;
		return true;
	}
	
	
}
