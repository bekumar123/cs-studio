package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import java.io.Serializable;
import java.util.Date;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;

public abstract class BasePlantUnitValue implements Serializable {

	private static final long serialVersionUID = -9084650768939540265L;

	private Date timeStamp;

	private boolean writeable;

	public BasePlantUnitValue() {
	}

	public BasePlantUnitValue(boolean writable) {
		setWritable(writable);
	}

	public void setTimeStamp(Date time) {
		this.timeStamp = time;
	}

	public Date getTimeStamp() {
		return this.timeStamp;
	}

	public void setWritable(boolean writable) {
		this.writeable = writable;
	}

	public boolean isWritable() {
		return writeable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (writeable ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasePlantUnitValue other = (BasePlantUnitValue) obj;
		if (writeable != other.writeable)
			return false;
		return true;
	}

	public abstract Object getData();

	@Override
	public String toString() {
		return ", timeStamp: " + getTimeStamp()
				+ ", isWritable: " + isWritable();
	}

	public static boolean isTypeOfPlantUnitValue(Object value) {
		return value instanceof BooleanValue || value instanceof DoubleValue
				|| value instanceof IntegerValue
				|| value instanceof StringValue;
	}

	public static IPlantUnitValue<?> createValue(PlantUnitDataTypes dataType) {
		switch (dataType) {
		case BOOLEAN:
			return new BooleanValue();
		case DOUBLE:
			return new DoubleValue();
		case STRING:
			return new StringValue();
		case INT:
			return new IntegerValue();
		}
		return null;
	}
}
