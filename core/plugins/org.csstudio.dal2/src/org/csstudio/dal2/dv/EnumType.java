package org.csstudio.dal2.dv;

public class EnumType {

	private String _name;
	private int _value;
	
	protected EnumType(String name, int value) {
		if (name == null) {
			throw new IllegalArgumentException("name must not be null");
		}
		_name = name;
		_value = value;
	}
	
	public String getName() {
		return _name;
	}
	
	public int getValue() {
		return _value;
	}

	public static EnumType valueOf(String name, int value) {
		return new EnumType(name, value);
	}
	
	@Override
	public int hashCode() {
		return _value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return _value == ((EnumType) obj)._value;
	}
	
	@Override
	public String toString() {
		return "EnumType [" + _value + ":" + _name + "]";
	}

}
