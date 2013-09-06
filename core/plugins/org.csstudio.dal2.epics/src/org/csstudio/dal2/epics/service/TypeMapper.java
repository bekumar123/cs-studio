package org.csstudio.dal2.epics.service;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Enum;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_String;

import java.nio.channels.IllegalSelectorException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal2.dv.Type;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TypeMapper provides the mapping of epics data types to dal2 data types. A
 * concrete mapper should be registered for every Type defined in {@link Type}
 * 
 * @param <T>
 */
public abstract class TypeMapper<T> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TypeMapper.class);

	private static final int CTRL_TYPE_OFFSET = 28;

	/** Seconds of epoch start since UTC time start. */
	public static long TS_EPOCH_SEC_PAST_1970 = 7305 * 86400;

	private final static Map<Type<?>, TypeMapper<?>> _typeMapper = new HashMap<Type<?>, TypeMapper<?>>();

	/**
	 * Mapping from dbrType to TypeMapper. Only primary mapper are added to this
	 * map.
	 * <p>
	 * Example:<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE}) is added<br/>
	 * ({@link DBRType#Double <-> {@link Type#DOUBLE_SEQ}) is <b>not</b> added
	 * <br/>
	 */
	private final static Map<DBRType, TypeMapper<?>> _dbr2mapper = new HashMap<DBRType, TypeMapper<?>>();

	private Type<T> _type;

	private DBRType _dbrType;

	private boolean _primary;

	static {
		registerMapper(new TypeMapper<Double>(Type.DOUBLE, DBRType.DOUBLE, true) {
			@Override
			public Double mapValue(DBR dbrValue) {
				return ((DBR_Double) dbrValue).getDoubleValue()[0];
			}
		});
		registerMapper(new TypeMapper<double[]>(Type.DOUBLE_SEQ,
				DBRType.DOUBLE, false) {
			@Override
			public double[] mapValue(DBR dbrValue) {
				return ((DBR_Double) dbrValue).getDoubleValue();
			}
		});
		registerMapper(new TypeMapper<Long>(Type.LONG, DBRType.INT, true) {
			@Override
			public Long mapValue(DBR dbrValue) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return (long) dbrInt.getIntValue()[0];
			}
		});
		registerMapper(new TypeMapper<long[]>(Type.LONG_SEQ, DBRType.INT, false) {
			@Override
			public long[] mapValue(DBR dbrValue) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				int[] intValues = dbrInt.getIntValue();

				long[] result = new long[intValues.length];
				for (int i = 0; i < intValues.length; i++) {
					result[i] = intValues[i];
				}

				return result;
			}
		});
		registerMapper(new TypeMapper<String>(Type.STRING, DBRType.STRING, true) {
			@Override
			public String mapValue(DBR dbrValue) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue()[0];
			}
		});
		registerMapper(new TypeMapper<String[]>(Type.STRING_SEQ, DBRType.STRING, false) {
			@Override
			public String[] mapValue(DBR dbrValue) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue();
			}
		});
		registerMapper(new TypeMapper<EpicsAlarmSeverity>(Type.SEVERITY,
				DBRType.ENUM, false) {
			@Override
			public EpicsAlarmSeverity mapValue(DBR dbrValue) {
				DBR_Enum dbrEnum = (DBR_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				return EpicsAlarmSeverity.valueOf(gov.aps.jca.dbr.Severity
						.forValue(enumValue));
			}
		});

		// registerMapper(new TypeMapper<String[]>(Type.NUMBER_SEQ,
		// DBRType.STRING) {
		// @Override
		// public String[] mapValue(DBR dbrValue) {
		// DBR_String dbrString = (DBR_String) dbrValue;
		// return dbrString.getStringValue();
		// }
		// });

		Set<Type<?>> checklist = new HashSet<Type<?>>(Type.listTypes());
		checklist.removeAll(_typeMapper.keySet());
		if (!checklist.isEmpty()) {
			LOGGER.warn("Incomplete type mapping: Missing mapping for "
					+ checklist + " in class TypeMapper");
		}
	}

	private static void registerMapper(TypeMapper<?> mapper) {
		_typeMapper.put(mapper.getType(), mapper);
		if (mapper.isPrimary()) {
			DBRType dbrType = mapper.getDBRType();

			if (_dbr2mapper.containsKey(dbrType)) {
				LOGGER.error("Invalid type mapping: Multiple primary type mapper using the same dbr type ("
						+ dbrType + ")");
			}

			_dbr2mapper.put(dbrType, mapper);
		}
	}

	/**
	 * Provides the mapper for a given dal2 type
	 * 
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> TypeMapper<T> getMapper(Type<T> type) {
		TypeMapper<?> mapper = _typeMapper.get(type);
		if (mapper == null) {
			throw new IllegalStateException("no mapper registred for type "
					+ type);
		}
		return (TypeMapper<T>) mapper;
	}

	/**
	 * Provides the suitable type for a given dbr type
	 * 
	 * @throws IllegalSelectorException
	 *             if no mapping is defined
	 */
	public static Type<?> getType(DBRType dbrType) {

		TypeMapper<?> result = _dbr2mapper.get(dbrType);
		if (result == null) {
			throw new IllegalStateException("No mapping defined for dbrType "
					+ dbrType);
		}
		return result.getType();
	}

	/**
	 * Constructor
	 * <p>
	 * <b>Note: Only one primary mapper is allowed for a dbr type</b>
	 * 
	 * @param type
	 *            the DAL type mapped by this mapper
	 * @param dbrType
	 *            the suitable dbr type
	 * @param flag
	 *            to indicate wether this is the primary mapper for the dbr
	 *            type.
	 * 
	 * @require type != null
	 * @require dbrType != null
	 */
	private TypeMapper(Type<T> type, DBRType dbrType, boolean primary) {
		assert type != null : "Precondition: type != null";
		assert dbrType != null : "Precondition: dbrType != null";

		_type = type;
		_dbrType = dbrType;
		_primary = primary;
	}

	/**
	 * Provides the dal2 type mapped by this mapper
	 * 
	 * @return
	 */
	public Type<T> getType() {
		return _type;
	}

	/**
	 * Provides the DBRType mapped by this mapper
	 */
	private DBRType getDBRType() {
		return _dbrType;
	}

	private boolean isPrimary() {
		return _primary;
	}

	/**
	 * Provide a corresponding DBR control type
	 */
	public DBRType getDBRCtrlType() {
		return DBRType.forValue(_dbrType.getValue() + CTRL_TYPE_OFFSET);
	}

	/**
	 * Maps an DBR value object to a given java type including a typecheck
	 * 
	 * @param dbrValue
	 *            the dbrValue to be mapped; must not be null
	 */
	public abstract T mapValue(DBR dbrValue);

	@Override
	public String toString() {
		return "TypeMapper: " + _dbrType + " <-> " + _type;
	}

}
