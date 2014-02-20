package org.csstudio.dal2.dv;

public class AbstractStringBasedDV {

	private final String _value;

	/**
	 * 
	 * @require value != null
	 */
	public AbstractStringBasedDV(String value) {
		assert value != null : "Precondition: value != null";
		_value = value;
	}

	public String getValue() {
		return _value;
	}

	public String toString() {
		return _value;
	};

	@Override
	public int hashCode() {
		return _value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		return _value.equals(((AbstractStringBasedDV) obj)._value);
	}

}
