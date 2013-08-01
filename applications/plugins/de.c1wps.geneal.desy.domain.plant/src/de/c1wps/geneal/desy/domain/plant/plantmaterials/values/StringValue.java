package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PvAttributeNames;

public class StringValue extends BasePlantUnitValue implements
		IPlantUnitValue<String>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6049021252200545484L;
	private String data;

	public StringValue() {
	}

	public StringValue(String data) {
		this.data = data;
	}

	@Override
	public void setData(Object val) {
		data = val.toString();
	}

	public boolean isValid(Object val) {
		return true;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		return PlantUnitDataTypes.STRING;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		StringValue other = (StringValue) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + ", value: " + getData() + ", dataType: "
				+ getDataType();
	}
}
