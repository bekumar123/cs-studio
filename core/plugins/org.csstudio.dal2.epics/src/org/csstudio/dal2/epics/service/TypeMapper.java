package org.csstudio.dal2.epics.service;

import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Byte;
import gov.aps.jca.dbr.DBR_Double;
import gov.aps.jca.dbr.DBR_Enum;
import gov.aps.jca.dbr.DBR_Float;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_LABELS_Enum;
import gov.aps.jca.dbr.DBR_Short;
import gov.aps.jca.dbr.DBR_String;

import java.nio.channels.IllegalSelectorException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.csstudio.dal2.dv.EnumType;
import org.csstudio.dal2.dv.Type;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
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
		registerMapper(new TypeMapper<Byte>(Type.BYTE, DBRType.BYTE, true) {
			@Override
			public Byte mapValue(DBR dbrValue) {
				return ((DBR_Byte) dbrValue).getByteValue()[0];
			}
		});
		registerMapper(new TypeMapper<byte[]>(Type.BYTE_SEQ, DBRType.BYTE,
				false) {
			@Override
			public byte[] mapValue(DBR dbrValue) {
				return ((DBR_Byte) dbrValue).getByteValue();
			}
		});
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
		registerMapper(new TypeMapper<Integer>(Type.LONG, DBRType.INT, true) {
			@Override
			public Integer mapValue(DBR dbrValue) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return dbrInt.getIntValue()[0];
			}
		});
		registerMapper(new TypeMapper<int[]>(Type.LONG_SEQ, DBRType.INT, false) {
			@Override
			public int[] mapValue(DBR dbrValue) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return dbrInt.getIntValue();
			}
		});
		registerMapper(new TypeMapper<Float>(Type.FLOAT, DBRType.FLOAT, true) {
			@Override
			public Float mapValue(DBR dbrValue) {
				DBR_Float dbr = (DBR_Float) dbrValue;
				return dbr.getFloatValue()[0];
			}
		});
		registerMapper(new TypeMapper<float[]>(Type.FLOAT_SEQ, DBRType.FLOAT,
				false) {
			@Override
			public float[] mapValue(DBR dbrValue) {
				DBR_Float dbr = (DBR_Float) dbrValue;
				return dbr.getFloatValue();
			}
		});
		registerMapper(new TypeMapper<Short>(Type.SHORT, DBRType.SHORT, true) {
			@Override
			public Short mapValue(DBR dbrValue) {
				DBR_Short dbr = (DBR_Short) dbrValue;
				return dbr.getShortValue()[0];
			}
		});
		registerMapper(new TypeMapper<short[]>(Type.SHORT_SEQ, DBRType.SHORT,
				false) {
			@Override
			public short[] mapValue(DBR dbrValue) {
				DBR_Short dbr = (DBR_Short) dbrValue;
				return dbr.getShortValue();
			}
		});
		registerMapper(new TypeMapper<String>(Type.STRING, DBRType.STRING, true) {
			@Override
			public String mapValue(DBR dbrValue) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue()[0];
			}
		});
		registerMapper(new TypeMapper<String[]>(Type.STRING_SEQ,
				DBRType.STRING, false) {
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
		registerMapper(new TypeMapper<EnumType>(Type.ENUM, DBRType.ENUM, false) {
			@Override
			public EnumType mapValue(DBR dbrValue) {
				DBR_LABELS_Enum dbrEnum = (DBR_LABELS_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				String name = dbrEnum.getLabels()[enumValue];
				return EnumType.valueOf(name, enumValue);
			}
		});

		registerMapper(new TypeMapper<EpicsEnum>(Type.EPICS_ENUM, DBRType.ENUM,
				true) {
			@Override
			public EpicsEnum mapValue(DBR dbrValue) {
				DBR_LABELS_Enum dbrEnum = (DBR_LABELS_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				String[] labels = dbrEnum.getLabels();
				if (labels != null) {
					if (enumValue > 15) {
						return EpicsEnum.createFromRaw((int) enumValue);
					} else {
						return EpicsEnum.createFromState(labels[enumValue],
								(int) enumValue);
					}
				} else {
					return EpicsEnum.createFromRaw((int) enumValue);
				}
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
		checklist.remove(Type.NATIVE);
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
	 * Creates a type mapper wrapping the mapper of the native type
	 * 
	 * @param nativeTypeMapper
	 * @return
	 */
	private static TypeMapper<Object> createWrapper(
			final TypeMapper<?> nativeTypeMapper) {
		return new TypeMapper<Object>(Type.NATIVE, DBRType.UNKNOWN, false) {
			@Override
			public Object mapValue(DBR dbrValue) {
				return nativeTypeMapper.mapValue(dbrValue);
			}

			@Override
			public DBRType getDBRCtrlType() {
				return nativeTypeMapper.getDBRCtrlType();
			}
		};
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
	 * Provides the mapper for a given type or uses the native type if the type
	 * is {@link Type#NATIVE}
	 * 
	 * @param type
	 *            the type
	 * @param nativeType
	 *            the native type
	 * @return
	 * 
	 * @require type != null
	 * @require nativeType != null
	 */
	@SuppressWarnings("unchecked")
	public static <T> TypeMapper<T> getMapper(Type<T> type, Type<?> nativeType) {

		assert type != null : "Precondition: type != null";
		assert nativeType != null : "Precondition: nativeType != null";

		TypeMapper<T> mapper;
		if (type == Type.NATIVE) {
			mapper = (TypeMapper<T>) createWrapper(getMapper(nativeType));
		} else {
			mapper = getMapper(type);
		}
		return mapper;
	}

	/**
	 * Provides the suitable type for a given dbr type
	 * 
	 * @throws IllegalSelectorException
	 *             if no mapping is defined
	 */
	public static Type<?> getType(DBRType dbrType) {

		TypeMapper<?> mapper = _dbr2mapper.get(dbrType);
		if (mapper == null) {
			throw new IllegalStateException("No mapping defined for dbrType "
					+ dbrType);
		}
		assert mapper.isPrimary() : "Check: mapper.isPrimary()";
		return mapper.getType();
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
	 * Provide a corresponding DBR control type, if such an type is available.
	 * Otherwise the "best known" type is provided.
	 */
	public DBRType getDBRCtrlType() {

		if (_dbrType.isENUM()) {
			// No CTRL type available for enum. Use best known.
			return DBRType.LABELS_ENUM;
		} else {
			return DBRType.forValue(_dbrType.getValue() + CTRL_TYPE_OFFSET);
		}
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
