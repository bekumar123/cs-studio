
package org.csstudio.nams.common.material.regelwerk;

public enum RegelErgebnis {
	ZUTREFFEND, NICHT_ZUTREFFEND, NOCH_NICHT_GEPRUEFT, VIELLEICHT_ZUTREFFEND;

	public boolean istEntschieden() {
		return ((this == ZUTREFFEND) || (this == NICHT_ZUTREFFEND));
	}
}
