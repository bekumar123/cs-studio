package de.c1wps.geneal.desy.domain.plant.plantmaterials;

public enum PVSeverityState {
	UNKNOWN, OK, MINOR, MAJOR, INVALID;
	
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
	
}
