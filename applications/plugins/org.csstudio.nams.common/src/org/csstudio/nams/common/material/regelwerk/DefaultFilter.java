package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.material.FilterId;

public class DefaultFilter implements Filter {

	private final FilterCondition rootCondition;
	private final FilterId filterId;

	public DefaultFilter(FilterId regelwerkskennung, FilterCondition regel) {
		this.filterId = regelwerkskennung;
		this.rootCondition = regel;
	}
	
	public FilterCondition getRegel() {
		return rootCondition;
	}

	@Override
	public FilterId getFilterId() {
		return filterId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rootCondition == null) ? 0 : rootCondition.hashCode());
		result = prime * result + ((filterId == null) ? 0 : filterId.hashCode());
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
		DefaultFilter other = (DefaultFilter) obj;
		if (rootCondition == null) {
			if (other.rootCondition != null)
				return false;
		} else if (!rootCondition.equals(other.rootCondition))
			return false;
		if (filterId == null) {
			if (other.filterId != null)
				return false;
		} else if (!filterId.equals(other.filterId))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Filter " + filterId + ", conditions: " + rootCondition;
	}

}
