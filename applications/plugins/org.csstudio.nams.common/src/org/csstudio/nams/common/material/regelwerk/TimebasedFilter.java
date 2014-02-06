package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.FilterId;

public class TimebasedFilter implements Filter {

	private final FilterCondition startCondition;
	private final FilterCondition stopCondition;
	private final Milliseconds timeout;
	private final TimeoutType timeoutType;
	private final FilterId filterId;

	public TimebasedFilter(FilterId regelwerkskennung, FilterCondition startRegel, FilterCondition stopRegel, Milliseconds timeout, TimeoutType timeoutType) {
		this.filterId = regelwerkskennung;
		this.startCondition = startRegel;
		this.stopCondition = stopRegel;
		this.timeout = timeout;
		this.timeoutType = timeoutType;
	}
	
	public FilterCondition getStartRegel() {
		return startCondition;
	}
	
	public FilterCondition getStopRegel() {
		return stopCondition;
	}

	public Milliseconds getTimeOut() {
		return timeout;
	}
	
	public TimeoutType getTimeoutType() {
		return timeoutType;
	}
	
	@Override
	public FilterId getFilterId() {
		return filterId;
	}
	
	@Override
	public String toString() {
		return "Timebased Filter " + filterId + ", start condition: " + 
	startCondition + ", stop condition: " + stopCondition;
	}
	
	public static enum TimeoutType {
		SENDE_BEI_TIMEOUT, SENDE_BEI_STOP_REGEL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterId == null) ? 0 : filterId.hashCode());
		result = prime * result + ((startCondition == null) ? 0 : startCondition.hashCode());
		result = prime * result + ((stopCondition == null) ? 0 : stopCondition.hashCode());
		result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
		result = prime * result + ((timeoutType == null) ? 0 : timeoutType.hashCode());
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
		TimebasedFilter other = (TimebasedFilter) obj;
		if (filterId == null) {
			if (other.filterId != null)
				return false;
		} else if (!filterId.equals(other.filterId))
			return false;
		if (startCondition == null) {
			if (other.startCondition != null)
				return false;
		} else if (!startCondition.equals(other.startCondition))
			return false;
		if (stopCondition == null) {
			if (other.stopCondition != null)
				return false;
		} else if (!stopCondition.equals(other.stopCondition))
			return false;
		if (timeout == null) {
			if (other.timeout != null)
				return false;
		} else if (!timeout.equals(other.timeout))
			return false;
		if (timeoutType != other.timeoutType)
			return false;
		return true;
	}
}
