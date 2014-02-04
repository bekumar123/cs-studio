package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.AlarmMessage;

public class NotFilterCondition implements FilterCondition {
	
	private FilterCondition childRegel;
	
	public NotFilterCondition(FilterCondition childRegel) {
		this.childRegel = childRegel;
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht) {
		return !childRegel.pruefeNachricht(nachricht);
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
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
		NotFilterCondition other = (NotFilterCondition) obj;
		if (childRegel == null) {
			if (other.childRegel != null)
				return false;
		} else if (!childRegel.equals(other.childRegel))
			return false;
		return true;
	}

}
