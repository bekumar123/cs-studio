package de.c1wps.geneal.desy.domain.plant.filter;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;

public interface IPlantInformationFilter {

	/**
	 * @param node
	 * 
	 * @return true, wenn der Knoten es durch den Filter schafft, sonst false
	 */
	public boolean filter(IPlantUnitNode node);
}
