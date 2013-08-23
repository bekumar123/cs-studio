package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.Characteristics;

public class CsPvData<T> {

	private T _value;
	private Characteristics _characteristics;

	public CsPvData(T value, Characteristics characteristics) {
		_value = value;
		_characteristics = characteristics;
	}

	public final T getValue() {
		return _value;
	}
	
	public final Characteristics getCharacteristics() {
		return _characteristics;
	}
	
}
