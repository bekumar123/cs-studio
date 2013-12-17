package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;


//CME: not well designed, fast implemented. (PlantUnitValue is not designed for values that handle a dynamic String value that can be different for same base values) 
public class EnumValue extends BasePlantUnitValue implements
		IPlantUnitValue<Integer>, Serializable {

	private static final long serialVersionUID = -7288715747910169514L;
	private Integer stateIndex;
	private String stringValue;

	public EnumValue() {
	}

	public EnumValue(Integer data) {
		this.stateIndex = data;
	}

	public EnumValue(Integer stateIndex, String stateString) {
		this.stateIndex = stateIndex;
		this.stringValue = stateString;
	}

	public void setData(Object val) {
		assert isValid(val) : "assert isValid(val)";
		if (val instanceof Integer) {
			stateIndex = (Integer) val;
		} else {
			int intVal = convertToInteger((String) val);
			setData(intVal);
		}
	}
	
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
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
	public Integer getData() {
		return stateIndex;
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		return PlantUnitDataTypes.STRING;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((stateIndex == null) ? 0 : stateIndex.hashCode());
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
		EnumValue other = (EnumValue) obj;
		if (stateIndex == null) {
			if (other.stateIndex != null)
				return false;
		} else if (!stateIndex.equals(other.stateIndex))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", value: " + getData() + ", dataType: "
				+ getDataType();
	}

	@Override
	public String getStringValue() {
		return stringValue;
	}

}
