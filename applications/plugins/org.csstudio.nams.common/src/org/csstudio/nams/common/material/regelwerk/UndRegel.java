package org.csstudio.nams.common.material.regelwerk;

import java.util.List;

import org.csstudio.nams.common.material.AlarmNachricht;

public class UndRegel implements Regel {

	private final List<Regel> regeln;
	
	public UndRegel(List<Regel> regeln) {
		this.regeln = regeln;
	}
	
	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht) {
		boolean result = regeln.size() > 0;
		for (Regel regel : this.regeln) {
			boolean regelErgebnis = regel.pruefeNachricht(nachricht);
			if(!regelErgebnis) {
				result = false;
				break;
			}
		}
		
		return result;
	}

	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht, AlarmNachricht vergleichsNachricht) {
		boolean result = regeln.size() > 0;
		for (Regel regel : this.regeln) {
			boolean regelErgebnis = regel.pruefeNachricht(nachricht, vergleichsNachricht);
			if(!regelErgebnis) {
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "Und( " + regeln + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regeln == null) ? 0 : regeln.hashCode());
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
		UndRegel other = (UndRegel) obj;
		if (regeln == null) {
			if (other.regeln != null)
				return false;
		} else if (!regeln.equals(other.regeln))
			return false;
		return true;
	}
	
	
}
