package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Status;

public enum PVAlarmStatus {
	NO_ALARM,
    READ,
    WRITE,
    HIHI,
    HIGH,
    LOLO,
    LOW,
    STATE,
    COS,
    COMM,
    TIMEOUT,
    HWLIMIT,
    CALC,
    SCAN,
    LINK,
    SOFT,
    BADSUB,
    UDF,
    DISABLE,
    SIMM,
    READACCESS,
    WRITEACCESS,
    UNKNOWN;
	
	
	/**
     * List of status by integer code, the index in the list corresponds to the JCA code.
     * @see Status
     */
    private static List<PVAlarmStatus> STATUS_BY_CODE =
        Arrays.asList(NO_ALARM,
                           READ,
                           WRITE,
                           HIHI,
                           HIGH,
                           LOLO,
                           LOW,
                           STATE,
                           COS,
                           COMM,
                           TIMEOUT,
                           HWLIMIT,
                           CALC,
                           SCAN,
                           LINK,
                           SOFT,
                           BADSUB,
                           UDF,
                           DISABLE,
                           SIMM,
                           READACCESS,
                           WRITEACCESS,
                           UNKNOWN);
    
    public int getEpicsCode() {
    	return STATUS_BY_CODE.indexOf(this);
    }

}
