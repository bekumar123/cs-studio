package de.c1wps.geneal.desy.domain.plant.filter;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;

public class PlantInformationTextFilter extends AbstractPlantInformationFilter {

	private String	filterText	= "";

	public PlantInformationTextFilter(String text) {
		if (text == null) {
			text = "";
		}
		filterText = text.toLowerCase();
	}

	@Override
	public boolean isEmpty() {
		return filterText.isEmpty();
	}

	@Override
	protected boolean matchesFilterConditions(IPlantUnitNode node) {
		return node.getDisplayName().toLowerCase().startsWith(filterText)
				|| node.getReferenceCode().toLowerCase().startsWith(filterText);
	}
}
