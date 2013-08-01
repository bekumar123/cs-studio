package de.c1wps.geneal.desy.domain.plant.filter;

import java.util.HashMap;
import java.util.Map;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;

public abstract class AbstractPlantInformationFilter implements IPlantInformationFilter {

	private Map<PlantNodeId, Boolean>	matchMap	= new HashMap<PlantNodeId, Boolean>();

	/**
	 * @return true, wenn die Menge der Filterbedingungen leer ist, sonst false.
	 */
	public abstract boolean isEmpty();

	/**
	 * Soll aufgerufen werden, wenn die Filterbedingungen neu gesetzt werden.
	 */
	protected void resetFilter() {
		matchMap.clear();
	}

	/**
	 * @param node
	 * @return true, wenn <code>node</code> selbst die Filterbedingungen
	 *         erfüllt, sonst false.
	 */
	protected abstract boolean matchesFilterConditions(IPlantUnitNode node);

	@Override
	public boolean filter(IPlantUnitNode node) {
		if (isEmpty()) {
			return true;
		}

		if (!matchMap.containsKey(node.getId())) {
			checkNode(node);
		}

		return matchMap.get(node.getId());
	}

	private void checkNode(IPlantUnitNode node) {
		boolean result = false;
		if (matchesFilterConditions(node)) {
			result = true;
			putDescendantsToMap(node, result);
		} else if (node.hasChildPlantUnitNodes()) {
			for (IPlantUnitNode child : node.getChildPlantUnitNodes()) {
				if (filter(child)) {
					result = true;
				}
			}
		}
		matchMap.put(node.getId(), result);
	}

	private void putDescendantsToMap(IPlantUnitNode node, boolean matches) {
		if (node.hasChildPlantUnitNodes()) {
			for (IPlantUnitNode child : node.getChildPlantUnitNodes()) {
				matchMap.put(child.getId(), matches);
				putDescendantsToMap(child, matches);
			}
		}
	}
}
