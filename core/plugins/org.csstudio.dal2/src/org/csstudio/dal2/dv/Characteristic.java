package org.csstudio.dal2.dv;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class Characteristic<T> {

	public static final Characteristic<Double> MAXIMUM = new Characteristic<Double>("MAXIMUM", Double.class);
	public static final Characteristic<Double> MINIMUM = new Characteristic<Double>("MINIMUM", Double.class);
	/**
	 * Graph Min (Lower Display Limit)
	 */
	public static final Characteristic<Double> GRAPH_MIN = new Characteristic<Double>("GRAPH_MIN", Double.class);
	/**
	 * Graph Max (Upper Display Limit)
	 */
	public static final Characteristic<Double> GRAPH_MAX = new Characteristic<Double>("GRAPH_MAX", Double.class);
	public static final Characteristic<Double> ALARM_MIN = new Characteristic<Double>("ALARM_MIN", Double.class);
	public static final Characteristic<Double> ALARM_MAX = new Characteristic<Double>("ALARM_MAX", Double.class);
	public static final Characteristic<Double> WARNING_MIN = new Characteristic<Double>("WARNING_MIN", Double.class);
	public static final Characteristic<Double> WARNING_MAX = new Characteristic<Double>("WARNING_MAX", Double.class);
	
	public static final Characteristic<String[]> LABELS = new Characteristic<String[]>("LABELS", String[].class);
	
	public static final Characteristic<EpicsAlarmSeverity> SEVERITY = new Characteristic<EpicsAlarmSeverity>("SEVERITY", EpicsAlarmSeverity.class);
	public static final Characteristic<EpicsAlarmStatus> STATUS = new Characteristic<EpicsAlarmStatus>("STATUS", EpicsAlarmStatus.class);
	public static final Characteristic<Timestamp> TIMESTAMP = new Characteristic<Timestamp>("TIMESTAMP", Timestamp.class);
	
	public static final Characteristic<String> HOSTNAME = new Characteristic<String>("HOSTNAME", String.class);

	private Class<T> _type;
	private String _name;

	private Characteristic(String name, Class<T> type) {
		_type = type;
		_name = name;
	}

	public Class<T> getType() {
		return _type;
	}
	
	@Override
	public String toString() {
		return _name + "[" + _type.getSimpleName() + "]";
	}
	
}
