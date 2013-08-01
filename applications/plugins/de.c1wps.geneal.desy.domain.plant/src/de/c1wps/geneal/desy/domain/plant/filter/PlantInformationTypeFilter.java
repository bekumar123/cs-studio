package de.c1wps.geneal.desy.domain.plant.filter;

import java.util.ArrayList;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSectionType;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;

public class PlantInformationTypeFilter extends AbstractPlantInformationFilter {

	private List<PlantUnitId> plantSectionTypeIds;

	public PlantInformationTypeFilter(List<PlantSectionType> plantSectionTypes) {
		plantSectionTypeIds = new ArrayList<PlantUnitId>();
		for (PlantSectionType type : plantSectionTypes) {
			plantSectionTypeIds.add(type.getId());
		}
	}

	public boolean containsType(PlantSectionType type) {
		return plantSectionTypeIds.contains(type.getId());
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	protected boolean matchesFilterConditions(IPlantUnitNode node) {
		if (node.hasType()) {
			return plantSectionTypeIds.contains(node.getTypeId());
		} else {
			return false;
		}
	}
}
