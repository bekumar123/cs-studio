package org.csstudio.nams.common.material.regelwerk.yaams;

import java.util.List;

import org.csstudio.nams.common.material.AlarmNachricht;

public class OderRegel implements Regel {

	private final List<Regel> regeln;
	
	public OderRegel(List<Regel> regeln) {
		this.regeln = regeln;
	}
	
	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht) {
		boolean result = false;
		
		for (Regel regel : this.regeln) {
			boolean regelErgebnis = regel.pruefeNachricht(nachricht);
			if(regelErgebnis) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	@Override
	public boolean pruefeNachricht(AlarmNachricht nachricht, AlarmNachricht vergleichsNachricht) {
		boolean result = false;
		
		for (Regel regel : this.regeln) {
			boolean regelErgebnis = regel.pruefeNachricht(nachricht, vergleichsNachricht);
			if(regelErgebnis) {
				result = true;
				break;
			}
		}
		
		return result;
	}

}
