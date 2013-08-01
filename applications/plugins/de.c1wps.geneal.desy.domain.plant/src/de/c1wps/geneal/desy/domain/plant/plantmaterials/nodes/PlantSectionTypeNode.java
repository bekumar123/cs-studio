package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSectionType;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

public class PlantSectionTypeNode implements IPlantUnitNode, Serializable {

	private static final long serialVersionUID = 6754537939331680235L;

	private PlantNodeId nodeId;

	private PlantSectionType type;

	private PlantTypeRootNode parent;

	private List<PlantSectionTemplateNode> plantSectionTemplateNodes;
	private List<ProcessVariableTemplateNode> processVariableTemplateNodes;

	public PlantSectionTypeNode(PlantNodeId id, PlantSectionType type) {
		assert id != null : "id != null";
		assert type != null : "type != null";

		nodeId = id;
		this.type = type;
		plantSectionTemplateNodes = new ArrayList<PlantSectionTemplateNode>();
		processVariableTemplateNodes = new ArrayList<ProcessVariableTemplateNode>();
	}

	public PlantNodeId getId() {
		return nodeId;
	}

	@Override
	public String getDisplayName() {
		return type.getDisplayName();
	}

	@Override
	public String getReferenceCode() {
		return "";
	}

	private void createProcessVariableTemplateNodeCopy(PlantSectionTypeNode copy) {
		for (ProcessVariableTemplateNode templateNode : processVariableTemplateNodes) {
			if (copy.canAddChildPlantUnitNode(templateNode)) {
				copy.addChildPlantUnitNode(templateNode);
			}
		}
	}

	private void createPlantSectionTemplateNodeCopy(PlantSectionTypeNode copy) {
		for (PlantSectionTemplateNode templateNode : plantSectionTemplateNodes) {
			if (copy.canAddChildPlantUnitNode(templateNode)) {
				copy.addChildPlantUnitNode(templateNode);
			}
		}
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
		return hasPlantSectionTemplateNodes()
				|| hasProcessVariableTemplateNodes();
	}

	private boolean hasPlantSectionTemplateNodes() {
		return !plantSectionTemplateNodes.isEmpty();
	}

	public boolean hasProcessVariableTemplateNodes() {
		return !processVariableTemplateNodes.isEmpty();
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		List<IPlantUnitNode> children = new ArrayList<IPlantUnitNode>(
				plantSectionTemplateNodes.size()
						+ processVariableTemplateNodes.size());
		children.addAll(plantSectionTemplateNodes);
		children.addAll(processVariableTemplateNodes);

		return children;
	}

	public List<ProcessVariableTemplateNode> getProcessVariableTemplateNodes() {
		return new ArrayList<ProcessVariableTemplateNode>(
				processVariableTemplateNodes);
	}
	
	public List<PlantSectionTemplateNode> getPlantSectionTemplateNodes() {
		return new ArrayList<PlantSectionTemplateNode>(
				plantSectionTemplateNodes);
	}

	public IPlantUnit findPlantUnitForId(PlantUnitId id) {
		if (getPlantUnit().getId().equals(id)) {
			return getPlantUnit();
		}

		for (IPlantUnitNode node : getChildPlantUnitNodes()) {
			if (node.getPlantUnit().getId().equals(id)) {
				return node.getPlantUnit();
			}
		}

		return null;
	}

	@Override
	public PlantSectionType getPlantUnit() {
		return type;
	}

	@Override
	public boolean canAddChildPlantUnitNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTemplateNode) {
			return canAddPlantSectionTemplateNode((PlantSectionTemplateNode) node);
		} else if (node instanceof ProcessVariableTemplateNode) {
			return canAddProcessVariableTemplateNode((ProcessVariableTemplateNode) node);
		}

		return false;
	}

	private boolean canAddPlantSectionTemplateNode(PlantSectionTemplateNode node) {
		return !plantSectionTemplateNodes.contains(node);
	}

	private boolean canAddProcessVariableTemplateNode(
			ProcessVariableTemplateNode node) {
		return !processVariableTemplateNodes.contains(node);
	}

	@Override
	public void addChildPlantUnitNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTemplateNode) {
			((PlantSectionTemplateNode) node).setParent(this);
		} else if (node instanceof ProcessVariableTemplateNode) {
			((ProcessVariableTemplateNode) node).setParent(this);
		}
	}

	void internalAddTemplateNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTemplateNode) {
			plantSectionTemplateNodes.add((PlantSectionTemplateNode) node);
		} else if (node instanceof ProcessVariableTemplateNode) {
			processVariableTemplateNodes
					.add((ProcessVariableTemplateNode) node);
		}
	}

	void internalRemoveTemplateNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTemplateNode) {
			plantSectionTemplateNodes.remove(node);
		} else if (node instanceof ProcessVariableTemplateNode) {
			processVariableTemplateNodes.remove(node);
		}
	}

	@Override
	public void delete() {
		if (hasParent()) {
			getParent().internalRemoveTypeNode(this);
			parent = null;
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

	@Override
	public IPlantUnitNode createClearCopy() {
		throw new UnsupportedOperationException();
	}

	@Override
	public PlantSectionTypeNode copyDeep() {
		PlantSectionTypeNode copy = new PlantSectionTypeNode(getId(),
				getPlantUnit().copyDeep());

		createPlantSectionTemplateNodeCopy(copy);

		createProcessVariableTemplateNodeCopy(copy);

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

		PlantSectionTypeNode other = (PlantSectionTypeNode) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;

		if (plantSectionTemplateNodes == null) {
			if (other.plantSectionTemplateNodes != null)
				return false;
		} else if (!plantSectionTemplateNodes
				.equals(other.plantSectionTemplateNodes))
			return false;

		if (processVariableTemplateNodes == null) {
			if (other.processVariableTemplateNodes != null)
				return false;
		} else if (!processVariableTemplateNodes
				.equals(other.processVariableTemplateNodes))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName() + ", Type=" + getTypeName()
				+ ", PSTemplateNodes=" + plantSectionTemplateNodes.size()
				+ ", PVTemplateNodes=" + processVariableTemplateNodes.size();
	}

	@Override
	public boolean hasType() {
		return getPlantUnit() != null;
	}
}
