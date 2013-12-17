package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;

public interface IPlantUnitValue<ValueType> {
	
	boolean isWritable();

	void setData(Object val);

	ValueType getData();

	boolean isValid(Object val);

	void setWritable(boolean writable);
	
	PlantUnitDataTypes getDataType();
	
	String getStringValue(); //CME: Diploma thesis comment: the string representation of a Value should maybe be handled by PlantInformationModel
}
