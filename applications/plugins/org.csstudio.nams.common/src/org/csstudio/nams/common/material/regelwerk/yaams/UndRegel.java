package org.csstudio.nams.common.material.regelwerk.yaams;

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
}
