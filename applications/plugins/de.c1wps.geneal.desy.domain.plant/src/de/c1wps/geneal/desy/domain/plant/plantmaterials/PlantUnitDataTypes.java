package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

public enum PlantUnitDataTypes implements Serializable {

	INT(Integer.class),
	STRING(String.class),
	DOUBLE(Double.class),
	BOOLEAN(Boolean.class),
	UNKNOWN(Object.class);

	private Class<?> type;

	private PlantUnitDataTypes(Class<?> dataType) {
		type = dataType;
	}

	public static PlantUnitDataTypes getByName(String name) {
		try {
			return valueOf(name);
		} catch (IllegalArgumentException e) {
			return STRING;
		}
	}

	public Class<?> getPrimitiveDataType() {
		return type;
	}
}
