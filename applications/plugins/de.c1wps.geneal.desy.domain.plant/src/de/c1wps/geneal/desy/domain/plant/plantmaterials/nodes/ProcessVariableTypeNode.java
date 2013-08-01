package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableType;

public class ProcessVariableTypeNode implements IPlantUnitNode, Serializable {

	private static final long serialVersionUID = -2014118680185553735L;

	private PlantNodeId id;

	private PlantTypeRootNode parent;

	private final ProcessVariableType processVariableType;

	public ProcessVariableTypeNode(PlantNodeId id,
			ProcessVariableType processVariableType) {
		assert id != null : "id != null";
		assert processVariableType != null : "processVariableType != null";

		this.id = id;
		this.processVariableType = processVariableType;
	}

	public PlantNodeId getId() {
		return id;
	}

	public String getDisplayName() {
		return getPlantUnit().getDisplayName();
	}

	@Override
	public String getReferenceCode() {
		return "";
	}

	@Override
	public String getTypeName() {
		return getPlantUnit().getDisplayName();
	}

	@Override
	public PlantUnitId getTypeId() {
		return getPlantUnit().getId();
	}

	@Override
	public PlantTypeRootNode getParent() {
		assert hasParent() : "hasParent()";

		return parent;
	}

	@Override
	public boolean hasParent() {
		return parent != null;
	}

	@Override
	public boolean hasChildPlantUnitNodes() {
		return false;
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		assert hasChildPlantUnitNodes() : "hasChildNodes()";

		return null;
	}

	@Override
	public ProcessVariableType getPlantUnit() {
		return processVariableType;
	}

	@Override
	public PlantUnitReference getPlantUnitReference() {
		assert hasPlantUnitReference() : "hasPlantUnitReference()";

		// Soweit sollte es nie kommen
		return null;
	}

	@Override
	public boolean hasPlantUnitReference() {
		return false;
	}

	@Override
	public boolean canAddChildPlantUnitNode(IPlantUnitNode node) {
		return false;
	}

	@Override
	public void addChildPlantUnitNode(IPlantUnitNode node) {
	}

	@Override
	public IPlantUnitNode createClearCopy() {
		throw new UnsupportedOperationException(
				"Kein ClearCopy von Typen bis jetzt moeglich");
	}

	@Override
	public void delete() {
		if (hasParent()) {
			getParent().internalRemoveTypeNode(this);
		}
	}

	void setParent(PlantTypeRootNode newParent) {
		assert newParent != null : "newParent != null";

		if (hasParent()) {
			getParent().internalRemoveTypeNode(this);
		}
		newParent.internalAddTypeNode(this);

		parent = newParent;
	}

	public static IPlantUnitNode createPlantUnitNodeFromTypeNode(
			ProcessVariableTypeNode templateTypeNode) {
		return new ProcessVariableNode(new PlantNodeId(), new ProcessVariable(
				new PlantUnitId(), templateTypeNode.getDisplayName()));

	}

	public ProcessVariableTypeNode copyDeep() {
		ProcessVariableTypeNode result = new ProcessVariableTypeNode(getId(),
				getPlantUnit().copyDeep());
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessVariableTypeNode other = (ProcessVariableTypeNode) obj;
		if (processVariableType == null) {
			if (other.processVariableType != null)
				return false;
		} else if (!processVariableType.equals(other.processVariableType))
			return false;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName() + ", Type=" + getTypeName()
				+ ", Parent=" + getParent();
	}

	@Override
	public boolean hasType() {
		return getPlantUnit() != null;
	}
}
