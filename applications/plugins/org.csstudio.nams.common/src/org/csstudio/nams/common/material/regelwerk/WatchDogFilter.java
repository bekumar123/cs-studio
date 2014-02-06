package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;

public class WatchDogFilter implements Filter {

	private FilterCondition rootCondition;
	private FilterId filterId;
	private Milliseconds timeout;

	public WatchDogFilter(FilterId regelwerkskennung,
			FilterCondition regel, Milliseconds timeout) {
				this.filterId = regelwerkskennung;
				this.rootCondition = regel;
				this.timeout = timeout;
	}

	@Override
	public FilterId getFilterId() {
		return filterId;
	}

	public FilterCondition getCondition() {
		return rootCondition;
	}

	public Milliseconds getTimeout() {
		return timeout;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
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
		WatchDogFilter other = (WatchDogFilter) obj;
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
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
		return "Watchdog filter " + filterId + ", timeout: " + timeout + ", conditions: " + rootCondition;
	}

}
