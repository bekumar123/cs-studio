package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSectionTemplate;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

public class PlantSectionTemplateNode implements IPlantUnitNode, Serializable {

	private static final long serialVersionUID = -1513779657365514099L;

	private PlantSectionTypeNode parent;

	private PlantSectionTemplate template;

	private final PlantNodeId id;

	public PlantSectionTemplateNode(PlantNodeId id,
			PlantSectionTemplate template) {

		assert id != null : "assert  id != null";
		assert template != null : "assert  template != null";

		this.id = id;
		this.template = template;
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
	public boolean hasChildPlantUnitNodes() {
		return false;
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		assert hasChildPlantUnitNodes() : "assert hasChildNodes()";
		return null;
	}

	@Override
	public PlantSectionTemplate getPlantUnit() {
		return template;
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

	@Override
	public PlantNodeId getId() {
		return id;
	}

	@Override
	public PlantSectionTypeNode getParent() {
		assert hasParent() : "assert hasParent()";
		return parent;
	}

	void setParent(PlantSectionTypeNode newParent) {
		assert newParent != null : "newParent != null";

		if (hasParent()) {
			getParent().internalAddTemplateNode(this);
		}
		newParent.internalAddTemplateNode(this);

		parent = newParent;
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
	public IPlantUnitNode createClearCopy() {
		PlantSectionTemplateNode clearCopy = new PlantSectionTemplateNode(
				new PlantNodeId(), getPlantUnit().createClearCopy());
		return clearCopy;
	}

	public PlantSectionTemplateNode copyDeep() {
		PlantSectionTemplateNode result = new PlantSectionTemplateNode(getId(),
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
		PlantSectionTemplateNode other = (PlantSectionTemplateNode) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getDisplayName() == null) {
			if (other.getDisplayName() != null)
				return false;
		}
		if (getTypeName() == null) {
			if (other.getTypeName() != null)
				return false;
		} else if (!getTypeName().equals(other.getTypeName()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlantSectionTemplateNode [name=" + getDisplayName()
				+ ", typeName=" + getTypeName() + ", id=" + getId() + "]";
	}

	@Override
	public boolean hasType() {
		return getPlantUnit() != null;
	}
}
