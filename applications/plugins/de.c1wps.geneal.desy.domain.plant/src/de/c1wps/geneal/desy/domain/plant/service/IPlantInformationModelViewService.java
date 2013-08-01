package de.c1wps.geneal.desy.domain.plant.service;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModelView;

public interface IPlantInformationModelViewService {

	PlantInformationModelView getViewForModel(
			PlantInformationModelId referecedModelId);
	
	void saveModelView(PlantInformationModelView view);
}
