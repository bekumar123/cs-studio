package de.c1wps.geneal.desy.domain.plant.plantinformationservice;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationTypeLibrary;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSectionTemplate;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSectionType;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableTemplate;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableType;
import de.c1wps.geneal.desy.domain.plant.service.PlantInformationServiceException;

public interface IPlantInformationTypeService {

	public PlantInformationTypeLibrary getPlantInformationTypeLibrary();

	public void savePlantInformationTypeLibrary(PlantInformationTypeLibrary plantSectionTypeModel)
			throws PlantInformationServiceException;

	public PlantSectionTemplate savePlantSectionTemplate(PlantSectionTemplate workingPlantSectionTemplate)
			throws PlantInformationServiceException;

	public ProcessVariableTemplate saveProcessVariableTemplate(ProcessVariableTemplate workingProcessVariableTemplate)
			throws PlantInformationServiceException;

	public PlantSectionType savePlantSectionType(PlantSectionType workingPlantSectionType) throws PlantInformationServiceException;

	public ProcessVariableType saveProcessVariableType(ProcessVariableType workingProcessVariableType)
			throws PlantInformationServiceException;

}
