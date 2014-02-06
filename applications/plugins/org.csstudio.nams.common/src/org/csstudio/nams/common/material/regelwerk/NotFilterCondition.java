package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.AlarmMessage;

public class NotFilterCondition implements FilterCondition {
	
	private FilterCondition childCondition;
	
	public NotFilterCondition(FilterCondition childRegel) {
		this.childCondition = childRegel;
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht) {
		return !childCondition.pruefeNachricht(nachricht);
	}

	@Override
	public boolean pruefeNachricht(AlarmMessage nachricht, AlarmMessage vergleichsNachricht) {
		return !childCondition.pruefeNachricht(nachricht, vergleichsNachricht);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((childCondition == null) ? 0 : childCondition.hashCode());
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
		if (childCondition == null) {
			if (other.childCondition != null)
				return false;
		} else if (!childCondition.equals(other.childCondition))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "!(" + childCondition + ")";
	}

}
