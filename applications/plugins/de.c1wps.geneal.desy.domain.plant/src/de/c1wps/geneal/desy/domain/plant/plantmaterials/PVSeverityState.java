package de.c1wps.geneal.desy.domain.plant.plantmaterials;

public enum PVSeverityState {
	UNKNOWN(0), OK(1), MINOR(2), MAJOR(3), INVALID(4);
	
	private final int _severityLevel;
	
	private PVSeverityState(int severityLevel) {
		_severityLevel = severityLevel;
	}
	
	public static PVSeverityState parseEpicsAlarmSeverity(String name) {
		if (name == null) {
            return PVSeverityState.OK;
        }
		if (name.equals("NO_ALARM")) {
			return PVSeverityState.OK;
		}
        try {
            return valueOf(name);
        } catch (final IllegalArgumentException e) {
            return PVSeverityState.OK;
        }
	}
	
	public int getSeverityLevel() {
		return _severityLevel;
	}
	
}
