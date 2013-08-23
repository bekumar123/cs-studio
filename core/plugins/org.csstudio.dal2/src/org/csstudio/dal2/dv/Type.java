package org.csstudio.dal2.dv;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;

/**
 * Data type used to connect to a process variable
 *
 * @param <T>
 */
public class Type<T> {

	private static Map<Class<?>, Type<?>> registredTypes = new HashMap<Class<?>, Type<?>>();

	public static final Type<EpicsAlarmSeverity> SEVERITY = new Type<EpicsAlarmSeverity>(EpicsAlarmSeverity.class);
	public static final Type<EnumType> ENUM = new Type<EnumType>(EnumType.class);

	public static final Type<String> STRING = new Type<String>(String.class);
	public static final Type<Double> DOUBLE = new Type<Double>(Double.class);
	public static final Type<Long> LONG = new Type<Long>(Long.class);
	
	public static final Type<String[]> STRING_SEQ = new Type<String[]>(String[].class);
	public static final Type<double[]> DOUBLE_SEQ = new Type<double[]>(double[].class);
	public static final Type<long[]> LONG_SEQ = new Type<long[]>(long[].class);
	
	private Class<? extends T> _javaType;
	
	/**
	 * @param javaType Java Type
	 */
	private Type(Class<? extends T> javaType) {
		assert javaType != null;
		_javaType = javaType;
		registredTypes.put(_javaType, this);
	}
	
	public static Collection<Type<?>> listTypes() {
		return Collections.unmodifiableCollection(registredTypes.values());
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Type<T> getType(Class<T> javaType) {
		Type<?> result = registredTypes.get(javaType);
		assert result.getJavaType().equals(javaType) : "Type check failed. Registred types are inconsistent: " + javaType;
		return (Type<T>)result;
	}
	
	public Class<? extends T> getJavaType() {
		assert !isSequenceType() || _javaType.isArray() : "Postcondition: IF isSequence() THEN result.isArray()";
		return _javaType;
	}
	
	public boolean isSequenceType() {
		return _javaType.isArray();
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
