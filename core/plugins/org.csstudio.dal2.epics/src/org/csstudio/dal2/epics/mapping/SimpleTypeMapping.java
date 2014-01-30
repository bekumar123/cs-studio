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

/**
 * The SimpleTypeMapping provides a set of DAL2 TypeMapper implementations
 * mapping from DBR to mainly built in java types.
 */
public class SimpleTypeMapping extends TypeMapping {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleTypeMapping.class);

	private static final SimpleTypeMapping instance = new SimpleTypeMapping();

	public static SimpleTypeMapping getInstance() {
		return instance;
	}

	public SimpleTypeMapping() {

		registerMapper(new TypeMapper<Byte>(Type.BYTE, DBRType.BYTE,
				MapperRole.PRIMARY) {
			@Override
			public Byte mapValue(DBR dbrValue, Characteristics characteristics) {
				byte[] bytes = ((DBR_Byte) dbrValue).getByteValue();
				return (bytes != null && bytes.length > 0) ? bytes[0] : null;
			}
		});
		registerMapper(new TypeMapper<Byte[]>(Type.BYTE_SEQ, DBRType.BYTE,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Byte[] mapValue(DBR dbrValue, Characteristics characteristics) {
				return ArrayUtils
						.toObject(((DBR_Byte) dbrValue).getByteValue());
			}
		});
		registerMapper(new TypeMapper<Double>(Type.DOUBLE, DBRType.DOUBLE,
				MapperRole.PRIMARY) {
			@Override
			public Double mapValue(DBR dbrValue, Characteristics characteristics) {
				double[] doubles = ((DBR_Double) dbrValue).getDoubleValue();
				return (doubles != null && doubles.length > 0) ? doubles[0]
						: null;
			}
		});
		registerMapper(new TypeMapper<Double[]>(Type.DOUBLE_SEQ,
				DBRType.DOUBLE, MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Double[] mapValue(DBR dbrValue,
					Characteristics characteristics) {
				return ArrayUtils.toObject(((DBR_Double) dbrValue)
						.getDoubleValue());
			}
		});
		registerMapper(new TypeMapper<Integer>(Type.LONG, DBRType.INT,
				MapperRole.PRIMARY) {
			@Override
			public Integer mapValue(DBR dbrValue,
					Characteristics characteristics) {
				int[] ints = ((DBR_Int) dbrValue).getIntValue();
				return (ints != null && ints.length > 0) ? ints[0] : null;
			}
		});
		registerMapper(new TypeMapper<Integer[]>(Type.LONG_SEQ, DBRType.INT,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Integer[] mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_Int dbrInt = (DBR_Int) dbrValue;
				return ArrayUtils.toObject(dbrInt.getIntValue());
			}
		});
		registerMapper(new TypeMapper<Float>(Type.FLOAT, DBRType.FLOAT,
				MapperRole.PRIMARY) {
			@Override
			public Float mapValue(DBR dbrValue, Characteristics characteristics) {
				float[] floats = ((DBR_Float) dbrValue).getFloatValue();
				return (floats != null && floats.length > 0) ? floats[0] : null;
			}
		});
		registerMapper(new TypeMapper<Float[]>(Type.FLOAT_SEQ, DBRType.FLOAT,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Float[] mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_Float dbr = (DBR_Float) dbrValue;
				return ArrayUtils.toObject(dbr.getFloatValue());
			}
		});
		registerMapper(new TypeMapper<Short>(Type.SHORT, DBRType.SHORT,
				MapperRole.PRIMARY) {
			@Override
			public Short mapValue(DBR dbrValue, Characteristics characteristics) {
				short[] shorts = ((DBR_Short) dbrValue).getShortValue();
				return (shorts != null && shorts.length > 0) ? shorts[0] : null;
			}
		});
		registerMapper(new TypeMapper<Short[]>(Type.SHORT_SEQ, DBRType.SHORT,
				MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public Short[] mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_Short dbr = (DBR_Short) dbrValue;
				return ArrayUtils.toObject(dbr.getShortValue());
			}
		});
		registerMapper(new TypeMapper<String>(Type.STRING, DBRType.STRING,
				MapperRole.PRIMARY) {
			@Override
			public String mapValue(DBR dbrValue, Characteristics characteristics) {
				String[] strings = ((DBR_String) dbrValue).getStringValue();
				return (strings != null && strings.length > 0) ? strings[0] : null;
			}
		});
		registerMapper(new TypeMapper<String[]>(Type.STRING_SEQ,
				DBRType.STRING, MapperRole.PRIMARY_SEQUENCE) {
			@Override
			public String[] mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_String dbrString = (DBR_String) dbrValue;
				return dbrString.getStringValue();
			}
		});
		registerMapper(new TypeMapper<EpicsAlarmSeverity>(Type.SEVERITY,
				DBRType.ENUM, MapperRole.ADDITIONAL) {
			@Override
			public EpicsAlarmSeverity mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_Enum dbrEnum = (DBR_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				return EpicsAlarmSeverity.valueOf(gov.aps.jca.dbr.Severity
						.forValue(enumValue));
			}
		});
		registerMapper(new TypeMapper<EpicsEnum>(Type.EPICS_ENUM, DBRType.ENUM,
				MapperRole.PRIMARY) {
			@Override
			public EpicsEnum mapValue(DBR dbrValue,
					Characteristics characteristics) {
				DBR_LABELS_Enum dbrEnum = (DBR_LABELS_Enum) dbrValue;
				short enumValue = dbrEnum.getEnumValue()[0];
				String[] labels = dbrEnum.getLabels();
				if (labels != null && enumValue < labels.length) {
					String label = labels[enumValue];
					if (label != null && !label.isEmpty()) {
						return EpicsEnum
								.createFromState(label, (int) enumValue);
					}
				}
				return EpicsEnum.createFromRaw((int) enumValue);

			}
		});

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
