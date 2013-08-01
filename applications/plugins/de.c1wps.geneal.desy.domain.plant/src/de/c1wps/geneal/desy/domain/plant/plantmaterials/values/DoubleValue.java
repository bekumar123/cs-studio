package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PvAttributeNames;

public class DoubleValue extends BasePlantUnitValue implements
		IPlantUnitValue<Double>, Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj)
			return true;

		if (getClass() != obj.getClass()) {
			if (isValid(obj)) {
				return true;
			} else {
				return false;
			}
		}
		DoubleValue other = (DoubleValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		else if (isWritable() != other.isWritable()) {
			return false;
		}

		return true;
	}

	private static final long serialVersionUID = 3804164073477331243L;
	private Double value = 0.0;

	public DoubleValue() {
	}

	public DoubleValue(double val) {
		setData(val);
	}

	public DoubleValue(double val, boolean writable) {
		setData(val);
		setWritable(writable);
	}

	public DoubleValue(Object val) {
		assert isValid(val) : "assert isValid(val)";
		setData(val);
	}

	public Double getData() {
		return value;
	}

	public void setData(Double val) {
		this.value = val;
	}

	public void setData(Object val) {
		assert isValid(val) : "assert isValid(val)";
		if (val instanceof Double) {
			value = (Double) val;
		} else if (val instanceof BasePlantUnitValue) {
			BasePlantUnitValue baseVal = (BasePlantUnitValue) val;
			setData(baseVal.getData());
		} else {
			Double doubleVal = convertToDouble((String) val);
			setData(doubleVal);
		}
	}

	public boolean isValid(Object val) {
		if (val == null) {
			return false;
		}

		if (val instanceof Double || val instanceof DoubleValue) {
			return true;
		} else if (val instanceof Integer) {
			return true;
		} else if (val instanceof String) {
			try {
				convertToDouble(val);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else if (val instanceof BasePlantUnitValue) {
			BasePlantUnitValue baseVal = (BasePlantUnitValue) val;
			try {
				convertToDouble(baseVal.getData());
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}

		return false;
	}

	protected static Double convertToDouble(Object val)
			throws NumberFormatException {
		assert val != null : "assert  val != null";

		if (val instanceof String && ((String) val).contains(",")) {
			String stringValue = (String) val;
			val = stringValue.replace(",", ".");
		}
		return Double.valueOf((String) val);
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		return PlantUnitDataTypes.DOUBLE;
	}

	@Override
	public String toString() {
		return super.toString() + ", value: " + getData() + ", dataType: "
				+ getDataType();
	}
}
