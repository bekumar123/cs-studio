package org.csstudio.dal.simple.compatibility;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class CompatibilityMapper {

	private static final Map<String, Characteristic<?>> NAME_TO_CHARACTERISTICS_MAP = new HashMap<String, Characteristic<?>>();
	static {
		NAME_TO_CHARACTERISTICS_MAP.put("alarmMax", Characteristic.ALARM_MAX);
		NAME_TO_CHARACTERISTICS_MAP.put("alarmMin", Characteristic.ALARM_MIN);
		NAME_TO_CHARACTERISTICS_MAP.put("graphMax", Characteristic.GRAPH_MAX);
		NAME_TO_CHARACTERISTICS_MAP.put("graphMin", Characteristic.GRAPH_MIN);
		NAME_TO_CHARACTERISTICS_MAP.put("enumDescriptions",
				Characteristic.LABELS);
		NAME_TO_CHARACTERISTICS_MAP.put("maximum", Characteristic.MAXIMUM);
		NAME_TO_CHARACTERISTICS_MAP.put("minimum", Characteristic.MINIMUM);
		NAME_TO_CHARACTERISTICS_MAP.put("severity", Characteristic.SEVERITY);
		NAME_TO_CHARACTERISTICS_MAP.put("statusInfo", Characteristic.STATUS);
		NAME_TO_CHARACTERISTICS_MAP.put("warningMax",
				Characteristic.WARNING_MAX);
		NAME_TO_CHARACTERISTICS_MAP.put("warningMin",
				Characteristic.WARNING_MIN);
	}
	
	public static Characteristic<?> toDal2Characteristic(String characteristicName) {
		return NAME_TO_CHARACTERISTICS_MAP.get(characteristicName);
	}

	public static DynamicValueCondition createDynamicValueCondition(
			IPvAccess<?> pvAccess) {
		Set<DynamicValueState> states = new HashSet<DynamicValueState>();
	
		Characteristics characteristics = pvAccess
				.getLastKnownCharacteristics();
		if (characteristics != null) {
	
			states.add(DynamicValueState.HAS_METADATA);
	
			EpicsAlarmStatus status = characteristics.get(Characteristic.STATUS);
			EpicsAlarmSeverity severity = characteristics.get(Characteristic.SEVERITY);
			if (status != null && status.isAlarm()) {
				switch (severity) {
				case MINOR:
					states.add(DynamicValueState.WARNING);
					break;
				case MAJOR:
					states.add(DynamicValueState.ALARM);
					break;
				case NO_ALARM:
				case UNKNOWN:
				case INVALID:
				}
			} else if (pvAccess.isConnected()) {
				states.add(DynamicValueState.NORMAL);
			}
		}
	
		if (pvAccess.getLastKnownValue() != null) {
			states.add(DynamicValueState.HAS_LIVE_DATA);
		}
		
		if (!pvAccess.isConnected()) {
			states.add(DynamicValueState.LINK_NOT_AVAILABLE);
		}
		
		// Timeout and Timelag are not used  
	
		return new DynamicValueCondition(EnumSet.copyOf(states));
	}
	

}
