package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelViewId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableNode;

public class PlantInformationModelView implements Serializable,
		IPlantInformationModelBrowser {

	private static final long serialVersionUID = 2734352331891783722L;
	private String displayName;
	private PlantInformationModelId referencedModelId;
	private PlantInformationModelViewId id;

	/* rootPLantSection hat keine PVs, nur PlantSections als Kinder */
	private PlantSectionNode rootPlantSection;

	/**
	 * @require {@link #isValid(PlantInformationModelId)}
	 * @param referencedModelId
	 * @param name
	 */
	public PlantInformationModelView(PlantInformationModelId referencedModelId,
			PlantInformationModelViewId id, String name) {
		this.referencedModelId = referencedModelId;
		this.id = id;
		this.displayName = name;

		rootPlantSection = new PlantSectionNode(new PlantNodeId(),
				new PlantSection(new PlantUnitId(), "root"));
		rootPlantSection.setModelId(referencedModelId);
	}

	public void addPlantSectionNode(PlantSectionNode plantSectionNode) {
		assert plantSectionNode != null : "plantSection != null";
		assert canAddPlantSectionNode(plantSectionNode) : "canAddPlantSection(plantSection)";

		plantSectionNode.setParent(rootPlantSection);
	}

	public boolean canAddPlantSectionNode(PlantSectionNode plantSectionNode) {
		return rootPlantSection.canAddChildPlantUnitNode(plantSectionNode);
	}

	public void setName(String newName) {
		displayName = newName;
	}

	public String getName() {
		return displayName;
	}

	public PlantSectionNode getRoot() {
		return rootPlantSection;
	}

	public IPlantUnitNode findNodeForPlantUnitId(PlantUnitId unitId) {
		return getRoot().findNodeByPlantUnitId(unitId);
	}

	public boolean containsNodeWithPlantUnitId(PlantUnitId unitId) {
		return findNodeForPlantUnitId(unitId) != null;
	}

	public IPlantUnit findPlantUnitById(PlantUnitId unitId) {
		IPlantUnitNode node = getRoot().findNodeByPlantUnitId(unitId);
		if (node != null) {
			return node.getPlantUnit();
		}

		return null;
	}

	public List<IPlantUnitNode> getAllPlantUnitNodes() {
		return getRoot().getAllDescendantPlantUnitNodes();
	}

	// public PlantInformationModel copyDeep() {
	// PlantInformationModel plantInformationModelCopy = new
	// PlantInformationModel(
	// new PlantInformationModelId(referencedModelId.getKey()), getName());
	//
	// plantInformationModelCopy.rootPlantSection = rootPlantSection
	// .copyDeep();
	// return plantInformationModelCopy;
	// }

	@Override
	public String toString() {
		return "PlantInformationModel [rootPlantSection=" + rootPlantSection
				+ ", modelId=" + referencedModelId + ", name=" + displayName
				+ "]";
	}

	public PlantInformationModelId getReferencedModelId() {
		return referencedModelId;
	}

	public boolean hasProcessVariableNodeWithControlSystemAddress(
			String addressPart) {
		assert addressPart != null : "addressPart != null";

		return getRoot().findProcessVariableNodeByControlSystemAddress(
				addressPart) != null;
	}

	/**
	 * 
	 * @param addressPart
	 * @require {@link #hasProcessVariableNodeWithControlSystemAddress(String)}
	 * @return
	 */
	public ProcessVariableNode getProcessVariableNodeForControlSystemAddress(
			String addressPart) {
		assert addressPart != null : "addressPart != null";
		assert hasProcessVariableNodeWithControlSystemAddress(addressPart) : "assert hasProcessVariableNodeWithControlSystemAddress(addressPart)";

		ProcessVariableNode processVariableNode = getRoot()
				.findProcessVariableNodeByControlSystemAddress(addressPart);

		return processVariableNode;
	}

	// public boolean checkDuplicate(ProcessVariable processVariable) {
	// boolean hasDuplicate = getRoot().checkDuplicate(processVariable);
	// /*
	// * eine PV kann sich nicht selbst als INCONSISTENT setzen, da wir erst
	// * das Duplikat identifizieren müssen bevor wir es setzen können
	// */
	// if (hasDuplicate) {
	// processVariable
	// .setConsistencyState(PVConsistencyState.INCONSISTENT);
	// } else {
	// processVariable.setConsistencyState(PVConsistencyState.CONSISTENT);
	// }
	//
	// return hasDuplicate;
	// }

	// /**
	// *
	// * @param alteredUnit
	// */
	// public void updatePlantUnit(IPlantUnit alteredUnit) {
	// IPlantUnit modelPlantUnit = findPlantUnitById(alteredUnit.getId());
	//
	// if (modelPlantUnit != null) {
	// if (alteredUnit instanceof ProcessVariable) {
	// checkDuplicate((ProcessVariable) alteredUnit);
	// }
	//
	// modelPlantUnit.update(alteredUnit);
	// }
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((referencedModelId == null) ? 0 : referencedModelId
						.hashCode());
		return result;
	}

	public PlantInformationModelView copyDeep() {
		PlantInformationModelView modelViewCopy = new PlantInformationModelView(
				getReferencedModelId(), id, getName());

		modelViewCopy.rootPlantSection = rootPlantSection.copyDeep();
		return modelViewCopy;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlantInformationModelView other = (PlantInformationModelView) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getRoot() == null) {
			if (other.getRoot() != null)
				return false;
		} else if (!getRoot().equals(other.getRoot()))
			return false;
		return true;
	}

	@Override
	public PlantInformationModelId getId() {
		return id;
	}
}
