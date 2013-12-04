package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;

public class BooleanValue extends BasePlantUnitValue implements
		IPlantUnitValue<Boolean>, Serializable {

	private static final long serialVersionUID = -69353678508576614L;
	private Boolean value;

	public BooleanValue() {
	}

	public BooleanValue(boolean val) {
		setData(val);
	}

	public void setData(boolean val) {
		this.value = val;
	}

	public void setData(Object val) {
		assert isValid(val) : "assert isValid(val)";
		if (val instanceof Boolean) {
			value = (Boolean) val;
		} else {
			Boolean booleanVal = convertToBoolean((String) val);
			setData(booleanVal);
		}
	}

	public Boolean getData() {
		return value;
	}

	@Override
	public boolean isValid(Object val) {
		if (val instanceof Boolean) {
			return true;
		} else if (val instanceof String) {
			String stringVal = (String) val;

			if (isTrueOrFalseString(stringVal)) {
				return true;
			} else if (isYesOrNoString(stringVal)) {
				return true;
			}
			return false;
		}
		return false;
	}

	private static boolean isYesOrNoString(String stringVal) {
		return stringVal.equalsIgnoreCase("yes")
				|| stringVal.equalsIgnoreCase("no");
	}

	private static boolean isTrueOrFalseString(String stringVal) {
		return stringVal.equalsIgnoreCase("true")
				|| stringVal.equalsIgnoreCase("false");
	}

	private Boolean convertToBoolean(String stringVal) {
		return Boolean.valueOf(stringVal);
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		return PlantUnitDataTypes.BOOLEAN;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BooleanValue other = (BooleanValue) obj;
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
