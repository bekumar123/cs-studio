package org.csstudio.nams.common.material.regelwerk;

import java.util.List;

import org.csstudio.nams.common.material.AlarmMessage;

public class OrFilterCondition implements FilterCondition {

	private final List<FilterCondition> regeln;
	
	public OrFilterCondition(List<FilterCondition> regeln) {
		this.regeln = regeln;
	}
	
	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht) {
		boolean result = false;

		// ACHTUNG: NullPointerException !!!
	    // Im Array this.regeln gibt es als Wert null (event. fehlende Timebased-Bedingung??)

		for (FilterCondition regel : this.regeln) {
			if(regel == null) {
				result = false;
				break;
			}
			boolean regelErgebnis = regel.pruefeNachricht(nachricht);
			if (regelErgebnis) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
		boolean result = false;
		
		for (FilterCondition regel : this.regeln) {
			boolean regelErgebnis = regel.pruefeNachricht(nachricht, vergleichsNachricht);
			if(regelErgebnis) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (regeln == null ? 0 : regeln.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        }
		if (obj == null) {
            return false;
        }
		if (getClass() != obj.getClass()) {
            return false;
        }
		OrFilterCondition other = (OrFilterCondition) obj;
		if (regeln == null) {
			if (other.regeln != null) {
                return false;
            }
		} else if (!regeln.equals(other.regeln)) {
            return false;
        }
		return true;
	}

	@Override
	public String toString() {
		return "or( " + regeln + ")";
	}

}
