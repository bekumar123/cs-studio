package org.csstudio.nams.common.material.regelwerk;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childRegel == null) ? 0 : childRegel.hashCode());
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
		NichtRegel other = (NichtRegel) obj;
		if (childRegel == null) {
			if (other.childRegel != null)
				return false;
		} else if (!childRegel.equals(other.childRegel))
			return false;
		return true;
	}

}
