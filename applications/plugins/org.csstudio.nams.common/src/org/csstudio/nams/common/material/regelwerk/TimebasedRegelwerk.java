package org.csstudio.nams.common.material.regelwerk;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.Regelwerkskennung;

public class TimebasedRegelwerk implements Regelwerk {

	private final Regel startRegel;
	private final Regel stopRegel;
	private final Millisekunden timeout;
	private final TimeoutType timeoutType;
	private final Regelwerkskennung regelwerkskennung;

	public TimebasedRegelwerk(Regelwerkskennung regelwerkskennung, Regel startRegel, Regel stopRegel, Millisekunden timeout, TimeoutType timeoutType) {
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

	public Millisekunden getTimeOut() {
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
}
