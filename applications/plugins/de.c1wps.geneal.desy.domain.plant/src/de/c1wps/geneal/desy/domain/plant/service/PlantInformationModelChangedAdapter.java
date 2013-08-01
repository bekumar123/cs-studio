package de.c1wps.geneal.desy.domain.plant.service;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModelView;

public abstract class PlantInformationModelChangedAdapter implements
		IPlantInformationModelChangedListener {

	@Override
	public void plantInformationModelChanged(PlantInformationModelId modelId) {
		// do nothing
	}

	@Override
	public void plantUnitChanged(PlantInformationModelId modelId,
			IPlantUnit unit) {
		// do nothing

	}

	@Override
	public void modelViewChanged(PlantInformationModelView modelView) {
		// do nothing

	}
	
	@Override
	public void modelViewAdded(PlantInformationModelView modelView) {
		// do nothing
	}

}
