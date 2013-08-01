package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableNode;

public interface IPlantInformationModelBrowser {

	String getName();

	PlantSectionNode getRoot();

	IPlantUnitNode findNodeForPlantUnitId(PlantUnitId unitId);

	IPlantUnit findPlantUnitById(PlantUnitId unitId);

	// FIXME er, 14.12.2011: gehören diese Methoden hier wirklich rein?
	ProcessVariableNode getProcessVariableNodeForControlSystemAddress(
			String addressPart);

	boolean hasProcessVariableNodeWithControlSystemAddress(String addressPart);
	
	PlantInformationModelId getId();
}
