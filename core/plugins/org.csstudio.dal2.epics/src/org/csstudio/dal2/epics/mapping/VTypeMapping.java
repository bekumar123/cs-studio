package org.csstudio.dal2.epics.mapping;

import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.dv.VTypes;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListInt;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.Display;
import org.epics.vtype.DisplayBuilder;
import org.epics.vtype.Time;
import org.epics.vtype.VInt;
import org.epics.vtype.VIntArray;
import org.epics.vtype.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VTypeMapping extends TypeMapping implements IEpicsTypeMapping {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(VTypeMapping.class);

	private static final VTypeMapping instance = new VTypeMapping();

	public static VTypeMapping getInstance() {
		return instance;
	}

	public VTypeMapping() {

//		registerMapper(new TypeMapper<Double>(Type.DOUBLE, DBRType.DOUBLE, true) {
//			@Override
//			public Double mapValue(DBR dbrValue, Characteristics characteristics) {
//				return ((DBR_Double) dbrValue).getDoubleValue()[0];
//			}
//		});
//		registerMapper(new TypeMapper<double[]>(Type.DOUBLE_SEQ,
//				DBRType.DOUBLE, false) {
//			@Override
//			public double[] mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				return ((DBR_Double) dbrValue).getDoubleValue();
//			}
//		});
		registerMapper(new TypeMapper<VInt>(VTypes.LONG, DBRType.INT, true) {
			@Override
			public VInt mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				int value = dbrInt.getIntValue()[0];
				Alarm alarm = mapAlarm(dbrInt);
				Time time = mapTime(dbrInt);
				Display display = mapDisplay(dbrInt);
				return ValueFactory.newVInt(value, alarm, time, display);
			}
		});
		registerMapper(new TypeMapper<VIntArray>(VTypes.LONG_SEQ, DBRType.INT, false) {
			@Override
			public VIntArray mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				Alarm alarm = mapAlarm(dbrInt);
				Time time = mapTime(dbrInt);
				Display display = mapDisplay(dbrInt);
				ListInt values = new ArrayInt(dbrInt.getIntValue());
				return ValueFactory.newVIntArray(values, alarm, time, display);
			}
		});
//		registerMapper(new TypeMapper<Float>(Type.FLOAT, DBRType.FLOAT, true) {
//			@Override
//			public Float mapValue(DBR dbrValue, Characteristics characteristics) {
//				DBR_Float dbr = (DBR_Float) dbrValue;
//				return dbr.getFloatValue()[0];
//			}
//		});
//		registerMapper(new TypeMapper<float[]>(Type.FLOAT_SEQ, DBRType.FLOAT,
//				false) {
//			@Override
//			public float[] mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				DBR_Float dbr = (DBR_Float) dbrValue;
//				return dbr.getFloatValue();
//			}
//		});
//		registerMapper(new TypeMapper<Short>(Type.SHORT, DBRType.SHORT, true) {
//			@Override
//			public Short mapValue(DBR dbrValue, Characteristics characteristics) {
//				DBR_Short dbr = (DBR_Short) dbrValue;
//				return dbr.getShortValue()[0];
//			}
//		});
//		registerMapper(new TypeMapper<short[]>(Type.SHORT_SEQ, DBRType.SHORT,
//				false) {
//			@Override
//			public short[] mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				DBR_Short dbr = (DBR_Short) dbrValue;
//				return dbr.getShortValue();
//			}
//		});
//		registerMapper(new TypeMapper<String>(Type.STRING, DBRType.STRING, true) {
//			@Override
//			public String mapValue(DBR dbrValue, Characteristics characteristics) {
//				DBR_String dbrString = (DBR_String) dbrValue;
//				return dbrString.getStringValue()[0];
//			}
//		});
//		registerMapper(new TypeMapper<String[]>(Type.STRING_SEQ,
//				DBRType.STRING, false) {
//			@Override
//			public String[] mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				DBR_String dbrString = (DBR_String) dbrValue;
//				return dbrString.getStringValue();
//			}
//		});
//		registerMapper(new TypeMapper<EpicsAlarmSeverity>(Type.SEVERITY,
//				DBRType.ENUM, false) {
//			@Override
//			public EpicsAlarmSeverity mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				DBR_Enum dbrEnum = (DBR_Enum) dbrValue;
//				short enumValue = dbrEnum.getEnumValue()[0];
//				return EpicsAlarmSeverity.valueOf(gov.aps.jca.dbr.Severity
//						.forValue(enumValue));
//			}
//		});
//		registerMapper(new TypeMapper<EnumType>(Type.ENUM, DBRType.ENUM, true) {
//			@Override
//			public EnumType mapValue(DBR dbrValue,
//					Characteristics characteristics) {
//				DBR_LABELS_Enum dbrEnum = (DBR_LABELS_Enum) dbrValue;
//				short enumValue = dbrEnum.getEnumValue()[0];
//				String name = dbrEnum.getLabels()[enumValue];
//				return EnumType.valueOf(name, enumValue);
//			}
//		});

		// registerMapper(new TypeMapper<String[]>(Type.NUMBER_SEQ,
		// DBRType.STRING) {
		// @Override
		// public String[] mapValue(DBR dbrValue) {
		// DBR_String dbrString = (DBR_String) dbrValue;
		// return dbrString.getStringValue();
		// }
		// });

		Collection<Type<?>> listTypes = Type.listTypes();
		Set<Type<?>> checklist = new HashSet<Type<?>>(listTypes);
		checklist.remove(Type.NATIVE);
		checklist.removeAll(getMappedTypes());
		if (!checklist.isEmpty()) {
			LOGGER.warn("Incomplete type mapping: Missing mapping for "
					+ checklist + " in class TypeMapper");
		}

	}

	public static Display mapDisplay(DBR dbr) {
		DisplayBuilder builder = new DisplayBuilder();
		if (dbr instanceof CTRL) {
			CTRL ctrl = (CTRL) dbr;
			builder.lowerDisplayLimit(ctrl.getLowerDispLimit().doubleValue());
			builder.lowerAlarmLimit(ctrl.getLowerAlarmLimit().doubleValue());
			builder.lowerWarningLimit(ctrl.getLowerWarningLimit().doubleValue());
			builder.lowerCtrlLimit(ctrl.getLowerCtrlLimit().doubleValue());
			builder.upperDisplayLimit(ctrl.getUpperDispLimit().doubleValue());
			builder.upperAlarmLimit(ctrl.getUpperAlarmLimit().doubleValue());
			builder.upperWarningLimit(ctrl.getUpperWarningLimit().doubleValue());
			builder.upperCtrlLimit(ctrl.getUpperCtrlLimit().doubleValue());
		}
		return builder.build();
	}

	public static Time mapTime(DBR dbr) {
		if (dbr instanceof Time) {
			org.epics.util.time.Timestamp timestamp = ((Time) dbr)
					.getTimestamp();
			Integer timeUserTag = ((Time) dbr).getTimeUserTag();
			return ValueFactory.newTime(timestamp, timeUserTag, true);
		} else {
			return ValueFactory.timeNow();
		}
	}

	public static Alarm mapAlarm(DBR dbr) {
		return ValueFactory.newAlarm(mapSeverity(dbr), mapStatus(dbr));
	}

	/**
	 * @param dbr
	 * @return
	 */
	public static String mapStatus(DBR dbr) {
		String alarmName = "N.A.";
		if (dbr instanceof STS) {
			STS sts = (STS) dbr;
			Status status = sts.getStatus();
			alarmName = status.getName();
		}
		return alarmName;
	}

	/**
	 * @param dbr
	 * @return
	 */
	public static AlarmSeverity mapSeverity(DBR dbr) {
		AlarmSeverity alarmSeverity = AlarmSeverity.UNDEFINED;
		if (dbr instanceof STS) {
			Severity severity = ((STS) dbr).getSeverity();
			if (severity == Severity.NO_ALARM) {
				alarmSeverity = AlarmSeverity.NONE;
			} else if (severity == Severity.MINOR_ALARM) {
				alarmSeverity = AlarmSeverity.MINOR;
			} else if (severity == Severity.MAJOR_ALARM) {
				alarmSeverity = AlarmSeverity.MAJOR;
			} else if (severity == Severity.INVALID_ALARM) {
				alarmSeverity = AlarmSeverity.INVALID;
			}
		}
		return alarmSeverity;
	}

}
