package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Milliseconds;
import org.csstudio.nams.common.material.Regelwerkskennung;

public class TimebasedRegelwerk implements Regelwerk {

	private final Regel startRegel;
	private final Regel stopRegel;
	private final Milliseconds timeout;
	private final TimeoutType timeoutType;
	private final Regelwerkskennung regelwerkskennung;

	public TimebasedRegelwerk(Regelwerkskennung regelwerkskennung, Regel startRegel, Regel stopRegel, Milliseconds timeout, TimeoutType timeoutType) {
		this.regelwerkskennung = regelwerkskennung;
		this.startRegel = startRegel;
		this.stopRegel = stopRegel;
		this.timeout = timeout;
		this.timeoutType = timeoutType;
	}
	
	public Regel getStartRegel() {
		return startRegel;
	}
	
	public Regel getStopRegel() {
		return stopRegel;
	}

	public Milliseconds getTimeOut() {
		return timeout;
	}
	
	public TimeoutType getTimeoutType() {
		return timeoutType;
	}
	
	@Override
	public Regelwerkskennung getRegelwerksKennung() {
		return regelwerkskennung;
	}
	
	@Override
	public String toString() {
		return "Regelwerk " + regelwerkskennung + ", Startregel: " + startRegel + ", Stopregel: " + stopRegel;
	}
	
	public static enum TimeoutType {
		SENDE_BEI_TIMEOUT, SENDE_BEI_STOP_REGEL;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((regelwerkskennung == null) ? 0 : regelwerkskennung.hashCode());
		result = prime * result + ((startRegel == null) ? 0 : startRegel.hashCode());
		result = prime * result + ((stopRegel == null) ? 0 : stopRegel.hashCode());
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
		TimebasedRegelwerk other = (TimebasedRegelwerk) obj;
		if (regelwerkskennung == null) {
			if (other.regelwerkskennung != null)
				return false;
		} else if (!regelwerkskennung.equals(other.regelwerkskennung))
			return false;
		if (startRegel == null) {
			if (other.startRegel != null)
				return false;
		} else if (!startRegel.equals(other.startRegel))
			return false;
		if (stopRegel == null) {
			if (other.stopRegel != null)
				return false;
		} else if (!stopRegel.equals(other.stopRegel))
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
