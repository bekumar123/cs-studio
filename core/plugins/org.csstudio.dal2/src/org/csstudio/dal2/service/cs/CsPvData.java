package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Type;

/**
 * The CsPvData represents a snapshot of a pv's state. It is used in communication
 * between dal2 and the control system specific dal2 plugin.
 * @param <T>
 */
public class CsPvData<T> {

	private T _value;
	private Characteristics _characteristics;
	private Type<?> _nativeType;

	public CsPvData(T value, Characteristics characteristics, Type<?> nativeType) {
		
		// This precondition looks strange but is intended. Expected is the concrete type used 
		// in the cs.
		// Type.NATIVE is only as a placeholder when the type is unknown and the native type
		// should be used.
		assert nativeType != Type.NATIVE : "Precondition violated: nativeType != Type.NATIVE";
		
		_value = value;
		_characteristics = characteristics;
		_nativeType = nativeType;
	}

	public final T getValue() {
		return _value;
	}
	
	public final Characteristics getCharacteristics() {
		return _characteristics;
	}
	
	public Type<?> getNativeType() {
		return _nativeType;
	}
	
	@Override
	public String toString() {
		return "CsPvData [value:" + _value + ", " + _characteristics + "]";
	}
	
}
