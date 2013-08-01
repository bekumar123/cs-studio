package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariableTemplate;

public class ProcessVariableTemplateNode implements IPlantUnitNode,
		Serializable {

	private static final long serialVersionUID = -1513779657365514099L;
	private PlantNodeId nodeId;

	private PlantSectionTypeNode parent;
	private ProcessVariableTemplate template;

	public ProcessVariableTemplateNode(PlantNodeId id,
			ProcessVariableTemplate template) {
		assert id != null : "id != null";
		assert template != null : "template != null";

		nodeId = id;
		this.template = template;
	}

	@Override
	public PlantNodeId getId() {
		return nodeId;
	}

	@Override
	public String getDisplayName() {
		return template.getDisplayName();
	}

	@Override
	public String getReferenceCode() {
		return "";
	}

	@Override
	public String getTypeName() {
		return getPlantUnit().getType().getDisplayName();
	}

	@Override
	public PlantUnitId getTypeId() {
		return getPlantUnit().getType().getId();
	}

	@Override
	public boolean hasParent() {
		return parent != null;
	}

	@Override
	public PlantSectionTypeNode getParent() {
		assert hasParent() : "assert hasParent()";
		return parent;
	}

	@Override
	public boolean hasChildPlantUnitNodes() {
		return false;
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		assert hasChildPlantUnitNodes() : "assert hasChildNodes()";
		return null;
	}

	@Override
	public ProcessVariableTemplate getPlantUnit() {
		return template;
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
		assert canAddChildPlantUnitNode(node) : "assert canAddPlantUnitNode(node)";
	}

	@Override
	public void delete() {
		if (hasParent()) {
			getParent().internalRemoveTemplateNode(this);
		}
	}

	void setParent(PlantSectionTypeNode newParent) {
		assert newParent != null : "newParent != null";

		if (hasParent()) {
			getParent().internalRemoveTemplateNode(this);
		}
		newParent.internalAddTemplateNode(this);

		parent = newParent;
	}

	@Override
	public IPlantUnitNode createClearCopy() {
		ProcessVariableTemplateNode clearCopy = new ProcessVariableTemplateNode(
				new PlantNodeId(), getPlantUnit().createClearCopy());
		return clearCopy;
	}

	public ProcessVariableTemplateNode copyDeep() {
		ProcessVariableTemplateNode copy = new ProcessVariableTemplateNode(
				getId(), getPlantUnit().copyDeep());

		return copy;
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
		ProcessVariableTemplateNode other = (ProcessVariableTemplateNode) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcessVariableTemplateNode [name=" + getDisplayName()
				+ ", typeName=" + getTypeName() + ", id=" + getId() + "]";
	}

	@Override
	public boolean hasType() {
		return getPlantUnit() != null;
	}
}
