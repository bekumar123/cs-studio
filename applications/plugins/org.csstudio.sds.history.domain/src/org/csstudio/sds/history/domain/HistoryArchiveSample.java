package org.csstudio.sds.history.domain;

import org.joda.time.DateTime;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVAlarmStatus;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PVSeverityState;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.values.IPlantUnitValue;


public class HistoryArchiveSample {
	
	private DateTime _timeStamp;
	
	private IPlantUnitValue<?> _value;
	
	private PVSeverityState _severityState;
	
	private PVAlarmStatus _alarmStatus;
	
	private HistoryArchiveSample() {
	}
	
	public static HistoryArchiveSample createHistoryArchiveSample(
			DateTime timeStamp,
			IPlantUnitValue<?> value,
			PVSeverityState severityState,
			PVAlarmStatus alarmStatus) {
		
		assert timeStamp != null;
		assert value != null;
		
		HistoryArchiveSample result = new HistoryArchiveSample();
		result._timeStamp = timeStamp;
		result._value = value;
		result._severityState = severityState;
		result._alarmStatus = alarmStatus;
		
		return result;
	}

	public static HistoryArchiveSample createHistoryArchiveSample(DateTime timeStamp, IPlantUnitValue<?> value) {
		assert timeStamp != null;
		assert value != null;
		
		HistoryArchiveSample result = new HistoryArchiveSample();
		result._timeStamp = timeStamp;
		result._value = value;
		result._severityState = PVSeverityState.INVALID;
		result._alarmStatus = PVAlarmStatus.UNKNOWN;
		
		return result;
	}
	
	public DateTime getTimeStamp() {
		return _timeStamp;
	}

	public IPlantUnitValue<?> getValue() {
		return _value;
	}

	public PVSeverityState getSeverityState() {
		return _severityState;
	}
	
	public void setPVSeverityState(PVSeverityState severityState) {
		assert severityState != null;
		_severityState = severityState;
	}

	public PVAlarmStatus getPvAlarmState() {
		return _alarmStatus;
	}
	
	public void setPvAlarmState(PVAlarmStatus alarmState) {
		assert alarmState != null;
		_alarmStatus = alarmState;
	}
	
	
}
