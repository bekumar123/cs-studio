package de.c1wps.geneal.desy.domain.plant.plantmaterials.values;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitDataTypes;

public class UnknownValue extends BasePlantUnitValue implements
		IPlantUnitValue<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Object data;

	public UnknownValue(Object val) {
		data = val;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public void setData(Object val) {
		data = val;
	}

	@Override
	public boolean isValid(Object val) {
		return false;
	}

	@Override
	public PlantUnitDataTypes getDataType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getStringValue() {
		return data.toString();
	}

}
