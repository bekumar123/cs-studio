package org.csstudio.dal2.epics.mapping;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.mapping.TypeMapper.MapperRole;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTypeMapping extends TypeMapping {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleTypeMapping.class);

	private static final SimpleTypeMapping instance = new SimpleTypeMapping();

	public static SimpleTypeMapping getInstance() {
		return instance;
	}

	public SimpleTypeMapping() {

		registerMapper(new TypeMapper<Byte>(Type.BYTE, DBRType.BYTE, MapperRole.PRIMARY) {
			@Override
			public Byte mapValue(DBR dbrValue, Characteristics characteristics) {
				return ((DBR_Byte) dbrValue).getByteValue()[0];
			}
		});
		registerMapper(new TypeMapper<byte[]>(Type.BYTE_SEQ, DBRType.BYTE,
				 MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public byte[] mapValue(DBR dbrValue, Characteristics characteristics) {
				return ((DBR_Byte) dbrValue).getByteValue();
			}
		});
		registerMapper(new TypeMapper<Double>(Type.DOUBLE, DBRType.DOUBLE,  MapperRole.PRIMARY) {
			@Override
			public Double mapValue(DBR dbrValue, Characteristics characteristics) {
				return ((DBR_Double) dbrValue).getDoubleValue()[0];
			}
		});
		registerMapper(new TypeMapper<Double[]>(Type.DOUBLE_SEQ,
				DBRType.DOUBLE, MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Double[] mapValue(DBR dbrValue, Characteristics characteristics) {
				return ArrayUtils.toObject(((DBR_Double) dbrValue).getDoubleValue());
			}
		});
		registerMapper(new TypeMapper<Integer>(Type.LONG, DBRType.INT,  MapperRole.PRIMARY) {
			@Override
			public Integer mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return dbrInt.getIntValue()[0];
			}
		});
		registerMapper(new TypeMapper<Integer[]>(Type.LONG_SEQ, DBRType.INT, MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Integer[] mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return ArrayUtils.toObject(dbrInt.getIntValue());
			}
		});
		registerMapper(new TypeMapper<Float>(Type.FLOAT, DBRType.FLOAT,  MapperRole.PRIMARY) {
			@Override
			public Float mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Float dbr = (DBR_Float) dbrValue;
				return dbr.getFloatValue()[0];
			}
		});
		registerMapper(new TypeMapper<float[]>(Type.FLOAT_SEQ, DBRType.FLOAT,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public float[] mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Float dbr = (DBR_Float) dbrValue;
				return dbr.getFloatValue();
			}
		});
		registerMapper(new TypeMapper<Short>(Type.SHORT, DBRType.SHORT,  MapperRole.PRIMARY) {
			@Override
			public Short mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Short dbr = (DBR_Short) dbrValue;
				return dbr.getShortValue()[0];
			}
		});
		registerMapper(new TypeMapper<short[]>(Type.SHORT_SEQ, DBRType.SHORT,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public short[] mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Short dbr = (DBR_Short) dbrValue;
				return dbr.getShortValue();
			}
		});
		registerMapper(new TypeMapper<String>(Type.STRING, DBRType.STRING,  MapperRole.PRIMARY) {
			@Override
			public String mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue()[0];
			}
		});
		registerMapper(new TypeMapper<String[]>(Type.STRING_SEQ,
				DBRType.STRING, MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public String[] mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue();
			}
		});
		registerMapper(new TypeMapper<EpicsAlarmSeverity>(Type.SEVERITY,
				DBRType.ENUM, MapperRole.ADDITIONAL) {
			@Override
			public EpicsAlarmSeverity mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_Enum dbrEnum = (DBR_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				return EpicsAlarmSeverity.valueOf(gov.aps.jca.dbr.Severity
						.forValue(enumValue));
			}
		});
		registerMapper(new TypeMapper<EpicsEnum>(Type.EPICS_ENUM, DBRType.ENUM, MapperRole.PRIMARY) {
			@Override
			public EpicsEnum mapValue(DBR dbrValue, Characteristics characteristics) {
				DBR_LABELS_Enum dbrEnum = (DBR_LABELS_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				String[] labels = dbrEnum.getLabels();
				if (labels != null && enumValue < labels.length) {
					String label = labels[enumValue];
					if (label != null && !label.isEmpty()) {
						return EpicsEnum.createFromState(label, (int)enumValue);
					}
				}
                return EpicsEnum.createFromRaw((int)enumValue);
				
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

		Collection<Type<?>> listTypes = Type.listTypes();
		Set<Type<?>> checklist = new HashSet<Type<?>>(listTypes);
		checklist.remove(Type.NATIVE);
		checklist.removeAll(getMappedTypes());
		if (!checklist.isEmpty()) {
			LOGGER.warn("Incomplete type mapping: Missing mapping for "
					+ checklist + " in class TypeMapper");
		}

	}

}
