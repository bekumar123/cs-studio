package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PVConsistencyState;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableNode;

/**
 * 
 * Represents the Plant information model with its' manipulating and probing
 * method and provides meta information about the model
 * 
 * @author GeneAL-Team
 * 
 */
public class PlantInformationModel implements IPlantInformationModelBrowser, Serializable {

	private static final long serialVersionUID = 7532314333097119006L;

	private String displayName;
	private PlantInformationModelId id;

	/* rootPLantSection hat keine PVs, nur PlantSections als Kinder */
	private PlantSectionNode rootPlantSection;

	/**
	 * @require {@link #isValid(PlantInformationModelId)}
	 * @param modelId
	 * @param name
	 */
	public PlantInformationModel(PlantInformationModelId modelId, String name) {
		this.id = modelId;
		this.displayName = name;

		rootPlantSection = new PlantSectionNode(new PlantNodeId(),
				new PlantSection(new PlantUnitId(), "root"));
		rootPlantSection.setModelId(modelId);
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

	public PlantInformationModel copyDeep() {
		PlantInformationModel plantInformationModelCopy = new PlantInformationModel(
				new PlantInformationModelId(id.getKey()), getName());

		plantInformationModelCopy.rootPlantSection = rootPlantSection
				.copyDeep();
		return plantInformationModelCopy;
	}

	@Override
	public String toString() {
		return "PlantInformationModel [rootPlantSection=" + rootPlantSection
				+ ", modelId=" + id + ", name=" + displayName + "]";
	}

	public PlantInformationModelId getId() {
		return id;
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

	public boolean checkDuplicate(ProcessVariable processVariable) {
		boolean hasDuplicate = getRoot().checkDuplicate(processVariable);
		/*
		 * eine PV kann sich nicht selbst als INCONSISTENT setzen, da wir erst
		 * das Duplikat identifizieren müssen bevor wir es setzen können
		 */
		if (hasDuplicate) {
			processVariable
					.setConsistencyState(PVConsistencyState.INCONSISTENT);
		} else {
			processVariable.setConsistencyState(PVConsistencyState.CONSISTENT);
		}

		return hasDuplicate;
	}

	/**
	 * 
	 * @param alteredUnit
	 */
	public void updatePlantUnit(IPlantUnit alteredUnit) {
		IPlantUnit modelPlantUnit = findPlantUnitById(alteredUnit.getId());

		if (modelPlantUnit != null) {
			if (alteredUnit instanceof ProcessVariable) {
				checkDuplicate((ProcessVariable) alteredUnit);
			}

			modelPlantUnit.update(alteredUnit);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlantInformationModel other = (PlantInformationModel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (rootPlantSection == null) {
			if (other.rootPlantSection != null)
				return false;
		} else if (!rootPlantSection.equals(other.rootPlantSection))
			return false;
		return true;
	}
}
