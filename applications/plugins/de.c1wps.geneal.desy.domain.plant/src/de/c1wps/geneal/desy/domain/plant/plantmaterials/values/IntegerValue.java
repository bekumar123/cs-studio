package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PvAttributeNames;

public class IntegerValue extends BasePlantUnitValue implements
		IPlantUnitValue<Integer>, Serializable {

	private static final long serialVersionUID = 1492492162628720095L;
	private Integer value;

	public IntegerValue() {
	}

	public IntegerValue(int val) {
		setData(val);
	}

	public IntegerValue(Object val) {
		assert isValid(val) : "assert isValid(val)";
		setData(val);
	}

	public void setData(int val) {
		this.value = val;
	}

	public Integer getData() {
		return value;
	}

	public void setData(Object val) {
		assert isValid(val) : "assert isValid(val)";
		if (val instanceof Integer) {
			value = (Integer) val;
		} else {
			int intVal = convertToInteger((String) val);
			setData(intVal);
		}
	}

	public boolean isValid(Object val) {
		if (val instanceof Integer) {
			return true;
		} else if (val instanceof String) {
			try {
				convertToInteger((String) val);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	private static int convertToInteger(String val)
			throws NumberFormatException {
		return Integer.valueOf(val);
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		return PlantUnitDataTypes.INT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (!super.equals(obj)) {
			return false;
		}

		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerValue other = (IntegerValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", value: " + getData() + ", dataType: "
				+ getDataType();
	}
}
