package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;

public class WatchDogFilter implements Filter {

	private FilterCondition regel;
	private FilterId regelwerkskennung;
	private Milliseconds delay;

	public WatchDogFilter(FilterId regelwerkskennung,
			FilterCondition regel, Milliseconds timeout) {
				this.regelwerkskennung = regelwerkskennung;
				this.regel = regel;
				this.delay = timeout;
	}

	@Override
	public FilterId getFilterId() {
		return regelwerkskennung;
	}

	public FilterCondition getRegel() {
		return regel;
	}

	public Milliseconds getDelay() {
		return delay;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((delay == null) ? 0 : delay.hashCode());
		result = prime * result + ((regel == null) ? 0 : regel.hashCode());
		result = prime * result + ((regelwerkskennung == null) ? 0 : regelwerkskennung.hashCode());
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
		if (delay == null) {
			if (other.delay != null)
				return false;
		} else if (!delay.equals(other.delay))
			return false;
		if (regel == null) {
			if (other.regel != null)
				return false;
		} else if (!regel.equals(other.regel))
			return false;
		if (regelwerkskennung == null) {
			if (other.regelwerkskennung != null)
				return false;
		} else if (!regelwerkskennung.equals(other.regelwerkskennung))
			return false;
		return true;
	}
}
