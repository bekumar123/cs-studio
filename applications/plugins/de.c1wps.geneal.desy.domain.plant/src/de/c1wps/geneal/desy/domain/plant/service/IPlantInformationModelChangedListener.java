package de.c1wps.geneal.desy.domain.plant.service;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModelView;

public interface IPlantInformationModelChangedListener {

	void plantInformationModelChanged(PlantInformationModelId modelId);

	void plantUnitChanged(PlantInformationModelId modelId, IPlantUnit unit);
	
	void modelViewChanged(PlantInformationModelView modelView);

	void modelViewAdded(PlantInformationModelView modelView);

}
