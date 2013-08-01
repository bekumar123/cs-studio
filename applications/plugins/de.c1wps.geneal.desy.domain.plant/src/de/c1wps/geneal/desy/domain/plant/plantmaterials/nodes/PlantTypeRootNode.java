package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

/**
 * Ist eine dedizierte Klasse, die den Wurzelknoten des Baumes bildet. Ein root
 * hat leicht andere Methoden als seine Kinderknoten, daher haben wir uns für
 * eine dedizierte Root-Klasse entschieden.
 * 
 * @author reiswich
 * 
 */
public class PlantTypeRootNode implements IPlantUnitNode, Serializable {

	private static final long				serialVersionUID	= -2329528832538404374L;

	private final PlantNodeId				id;
	private List<PlantSectionTypeNode>		plantSectionTypeNodes;
	private List<ProcessVariableTypeNode>	processVariableTypeNodes;

	public PlantTypeRootNode(PlantNodeId id) {
		this.id = id;
		plantSectionTypeNodes = new ArrayList<PlantSectionTypeNode>();
		processVariableTypeNodes = new ArrayList<ProcessVariableTypeNode>();
	}

	@Override
	public PlantNodeId getId() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return "Root";
	}

	@Override
	public String getReferenceCode() {
		return "";
	}

	@Override
	public String getTypeName() {
		return "";
	}

	@Override
	public PlantUnitId getTypeId() {
		throw new UnsupportedOperationException("Root node has no Type ID.");
	}

	@Override
	public IPlantUnitNode getParent() {
		assert hasParent() : "hasParent()";

		// Soweit sollte es nie kommen
		return null;
	}

	@Override
	public boolean hasParent() {
		return false;
	}

	@Override
	public boolean hasChildPlantUnitNodes() {
		return hasPlantSectionTypeNodes() || hasProcessVariableTypeNodes();
	}

	private boolean hasPlantSectionTypeNodes() {
		return !plantSectionTypeNodes.isEmpty();
	}

	private boolean hasProcessVariableTypeNodes() {
		return !processVariableTypeNodes.isEmpty();
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		ArrayList<IPlantUnitNode> children = new ArrayList<IPlantUnitNode>(plantSectionTypeNodes.size()
				+ processVariableTypeNodes.size());
		children.addAll(plantSectionTypeNodes);
		children.addAll(processVariableTypeNodes);

		return children;
	}

	public List<PlantSectionTypeNode> getPlantSectionTypeNodes() {
		return new ArrayList<PlantSectionTypeNode>(plantSectionTypeNodes);
	}

	public List<ProcessVariableTypeNode> getProcessVariableTypeNodes() {
		return new ArrayList<ProcessVariableTypeNode>(processVariableTypeNodes);
	}

	@Override
	public IPlantUnit getPlantUnit() {
		throw new UnsupportedOperationException("Root node has no PlantUnit.");
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
		if (node instanceof PlantSectionTypeNode) {
			return canAddPlantSectionTypeNode((PlantSectionTypeNode) node);
		} else if (node instanceof ProcessVariableTypeNode) {
			return canAddProcessVariableTypeNode((ProcessVariableTypeNode) node);
		}

		return false;
	}

	private boolean canAddPlantSectionTypeNode(PlantSectionTypeNode node) {
		return !plantSectionTypeNodes.contains(node);
	}

	private boolean canAddProcessVariableTypeNode(ProcessVariableTypeNode node) {
		return !processVariableTypeNodes.contains(node);
	}

	@Override
	public void addChildPlantUnitNode(IPlantUnitNode node) {
		assert canAddChildPlantUnitNode(node) : "canAddPlantUnitNode(node)";

		if (node instanceof PlantSectionTypeNode) {
			((PlantSectionTypeNode) node).setParent(this);
		} else if (node instanceof ProcessVariableTypeNode) {
			((ProcessVariableTypeNode) node).setParent(this);
		}
	}

	void internalAddTypeNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTypeNode) {
			plantSectionTypeNodes.add((PlantSectionTypeNode) node);
		} else if (node instanceof ProcessVariableTypeNode) {
			processVariableTypeNodes.add((ProcessVariableTypeNode) node);
		}
	}

	void internalRemoveTypeNode(IPlantUnitNode node) {
		if (node instanceof PlantSectionTypeNode) {
			plantSectionTypeNodes.remove(node);
		} else if (node instanceof ProcessVariableTypeNode) {
			processVariableTypeNodes.remove(node);
		}
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException("Cannot delete Root node.");
	}

	@Override
	public IPlantUnitNode createClearCopy() {
		throw new UnsupportedOperationException("Cannot copy Root node.");
	}

	@Override
	public IPlantUnitNode copyDeep() {
		throw new UnsupportedOperationException("Cannot copy Root node");
	}

	public IPlantUnit findPlantUnitForId(PlantUnitId id) {
		for (ProcessVariableTypeNode node : processVariableTypeNodes) {
			if (node.getPlantUnit().getId().equals(id)) {
				return node.getPlantUnit();
			}
		}

		for (PlantSectionTypeNode node : plantSectionTypeNodes) {
			IPlantUnit childNode = node.findPlantUnitForId(id);
			if (childNode != null) {
				return childNode;
			}
		}

		return null;
	}

	public PlantSectionTypeNode getPlantSectionTypeNodeForPlantUnitId(PlantUnitId id) {
		for (PlantSectionTypeNode node : plantSectionTypeNodes) {
			if (node.getPlantUnit().getId().equals(id)) {
				return node;
			}
		}
		return null;
	}

	public ProcessVariableTypeNode getProcessVariableTypeNodeForPlantUnitId(PlantUnitId id) {
		for (ProcessVariableTypeNode node : processVariableTypeNodes) {
			if (node.getPlantUnit().getId().equals(id)) {
				return node;
			}
		}
		return null;
	}

	@Override
	public boolean hasType() {
		return false;
	}

}
