package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.util.Arrays;
import java.util.List;

import javax.print.attribute.standard.Severity;

public enum PVSeverityState {
	UNKNOWN(0), OK(1), MINOR(2), MAJOR(3), INVALID(4);
	
	private final int _severityLevel;
	
	/**
     * List of severities by integer code, the index in the list corresponds to the JCA code.
     * @see Severity
     */
    private static List<PVSeverityState> SEVS_BY_CODE =
        Arrays.asList(OK, MINOR, MAJOR, INVALID);
	
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
	
	public int getEpicsCode() {
		switch (this) {
		case UNKNOWN:
			return SEVS_BY_CODE.indexOf(INVALID);
		case OK:
			return SEVS_BY_CODE.indexOf(OK);
		case MINOR:
			return SEVS_BY_CODE.indexOf(MINOR);
		case MAJOR:
			return SEVS_BY_CODE.indexOf(MAJOR);
		case INVALID:
			return SEVS_BY_CODE.indexOf(INVALID);
		default:
			return SEVS_BY_CODE.indexOf(INVALID);
		}
	}
	
}
