package org.csstudio.dal2.dv;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.types.EpicsEnum;

/**
 * Data type used to connect to a process variable
 * 
 * @param <T>
 */
public class Type<T> {

	private static Map<Class<?>, Type<?>> registredTypes = new HashMap<Class<?>, Type<?>>();

	/**
	 * The native type is used as a placeholder when the type is unknown when
	 * creating the pv access. It is called 'NATIVE' because in runtime the
	 * native type is used for the connection and for the mapping.
	 * <p>
	 * When using this type with a monitor might imply a recreation of the
	 * monitor after a reconnect to ensure the monitor is using the potentially
	 * changed native type.
	 */
	public static final Type<Object> NATIVE = new Type<Object>(Object.class);

	public static final Type<EpicsAlarmSeverity> SEVERITY = new Type<EpicsAlarmSeverity>(
			EpicsAlarmSeverity.class);

	public static final Type<EpicsEnum> EPICS_ENUM = new Type<EpicsEnum>(
			EpicsEnum.class);

	public static final Type<String[]> STRING_SEQ = new Type<String[]>(
			String[].class);
	public static final Type<Double[]> DOUBLE_SEQ = new Type<Double[]>(
			Double[].class);
	public static final Type<Byte[]> BYTE_SEQ = new Type<Byte[]>(Byte[].class);
	public static final Type<Integer[]> LONG_SEQ = new Type<Integer[]>(
			Integer[].class);
	public static final Type<Float[]> FLOAT_SEQ = new Type<Float[]>(
			Float[].class);
	public static final Type<Short[]> SHORT_SEQ = new Type<Short[]>(
			Short[].class);

	public static final Type<String> STRING = new Type<String>(String.class,
			STRING_SEQ);
	public static final Type<Double> DOUBLE = new Type<Double>(Double.class,
			DOUBLE_SEQ);
	public static final Type<Byte> BYTE = new Type<Byte>(Byte.class, BYTE_SEQ);
	public static final Type<Integer> LONG = new Type<Integer>(Integer.class,
			LONG_SEQ);
	public static final Type<Float> FLOAT = new Type<Float>(Float.class,
			FLOAT_SEQ);
	public static final Type<Short> SHORT = new Type<Short>(Short.class,
			SHORT_SEQ);

	private Class<? extends T> _javaType;

	/**
	 * Optional sequence type
	 */
	private Type<?> _sequenceType;

	/**
	 * @param javaType
	 *            Java Type
	 */
	public Type(Class<? extends T> javaType) {
		this(javaType, null);
	}

	/**
	 * @param javaType
	 *            Java Type
	 */
	public Type(Class<? extends T> javaType, Type<?> sequenceType) {
		assert javaType != null;
		_javaType = javaType;
		_sequenceType = sequenceType;
		registredTypes.put(_javaType, this);
	}

	public static Collection<Type<?>> listTypes() {
		return Collections.unmodifiableCollection(registredTypes.values());
	}

	@SuppressWarnings("unchecked")
	public static <T> Type<T> getType(Class<T> javaType) {
		Type<?> result = registredTypes.get(javaType);
		assert result == null || result.getJavaType().equals(javaType) : "Type check failed. Registred types are inconsistent: "
				+ javaType;
		return (Type<T>) result;
	}

	public Class<? extends T> getJavaType() {
		assert !isSequenceType() || _javaType.isArray() : "Postcondition: IF isSequence() THEN result.isArray()";
		return _javaType;
	}

	public boolean isSequenceType() {
		return _javaType.isArray();
	}

	/**
	 * provides the suitable sequence type or null
	 */
	public Type<?> getSequenceType() {
		return _sequenceType;
	}

	public Type<?> getComponentType() {
		assert isSequenceType() : "Precondition: isSequenceType()";
		Type<?> result = registredTypes.get(_javaType.getComponentType());
		assert result != null : "Postcondition: result != null";
		return result;
	}

	@Override
	public String toString() {
		return "Type (" + _javaType + ")";
	}
}
