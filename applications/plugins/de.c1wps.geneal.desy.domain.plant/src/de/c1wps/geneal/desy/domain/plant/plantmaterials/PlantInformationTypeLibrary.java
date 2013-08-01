package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationTypeLibraryId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.filter.PlantInformationTypeFilter;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionTemplateNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionTypeNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantTypeRootNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableTemplateNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableTypeNode;

public class PlantInformationTypeLibrary implements Serializable {

	private static final long serialVersionUID = -4962719229886498079L;

	private PlantInformationTypeLibraryId id;
	private PlantSectionType defaultType;
	private ProcessVariableType defaultProcessVariableType;

	private PlantTypeRootNode rootNode;

	private int processVariableCreationCount = 0;

	public PlantInformationTypeLibrary() {
		this.id = new PlantInformationTypeLibraryId(UUID.randomUUID());
		this.defaultType = PlantSectionType.NULL;
		this.defaultProcessVariableType = ProcessVariableType.NULL;
		this.rootNode = new PlantTypeRootNode(new PlantNodeId());
	}

	public PlantInformationTypeLibraryId getId() {
		return id;
	}

	private PlantTypeRootNode getRoot() {
		return rootNode;
	}

	public List<PlantSectionTypeNode> getAllPlantSectionTypeNodes() {
		return getRoot().getPlantSectionTypeNodes();
	}

	public List<PlantSectionTypeNode> getAllPlantSectionTypeNodesWithDefault() {
		PlantSectionTypeNode defaultTypeNode = new PlantSectionTypeNode(
				new PlantNodeId(), getDefaultType());
		List<PlantSectionTypeNode> realTypeNodes = getAllPlantSectionTypeNodes();

		List<PlantSectionTypeNode> allTypeNodes = new ArrayList<PlantSectionTypeNode>(
				realTypeNodes.size() + 1);
		allTypeNodes.add(defaultTypeNode);
		allTypeNodes.addAll(realTypeNodes);

		return allTypeNodes;
	}

	public List<PlantSectionType> getAllPlantSectionTypes() {
		ArrayList<PlantSectionType> types = new ArrayList<PlantSectionType>();
		for (PlantSectionTypeNode node : getAllPlantSectionTypeNodes()) {
			types.add(node.getPlantUnit());
		}

		return types;
	}

	public List<ProcessVariableTypeNode> getAllProcessVariableTypeNodes() {
		return getRoot().getProcessVariableTypeNodes();
	}

	public List<ProcessVariableTypeNode> getAllProcessVariableTypeNodesWithDefault() {
		ProcessVariableTypeNode defaultTypeNode = new ProcessVariableTypeNode(
				new PlantNodeId(), getDefaultProcessVariableType());
		List<ProcessVariableTypeNode> realTypeNodes = getAllProcessVariableTypeNodes();

		List<ProcessVariableTypeNode> allTypeNodes = new ArrayList<ProcessVariableTypeNode>(
				realTypeNodes.size() + 1);
		allTypeNodes.add(defaultTypeNode);
		allTypeNodes.addAll(realTypeNodes);

		return allTypeNodes;
	}

	public List<ProcessVariableType> getAllProcessVariableTypes() {
		ArrayList<ProcessVariableType> types = new ArrayList<ProcessVariableType>();
		for (ProcessVariableTypeNode node : getAllProcessVariableTypeNodes()) {
			types.add(node.getPlantUnit());
		}

		return types;
	}

	public PlantSectionType getDefaultType() {
		return defaultType;
	}

	public ProcessVariableType getDefaultProcessVariableType() {
		return defaultProcessVariableType;
	}

	private IPlantUnit getNodeForPlantUnitId(PlantUnitId id) {
		return getRoot().findPlantUnitForId(id);
	}

	public PlantSectionTypeNode getPlantSectionTypeNodeForPlantUnitId(
			PlantUnitId typeId) {
		return getRoot().getPlantSectionTypeNodeForPlantUnitId(typeId);
	}

	public PlantSectionType getPlantSectionTypeForPlantUnitId(PlantUnitId typeId) {
		PlantSectionTypeNode node = getPlantSectionTypeNodeForPlantUnitId(typeId);
		if (node != null) {
			return node.getPlantUnit();
		}

		return null;
	}

	public ProcessVariableTypeNode getProcessVariableTypeNodeForPlantUnitId(
			PlantUnitId typeId) {
		return getRoot().getProcessVariableTypeNodeForPlantUnitId(typeId);
	}

	public ProcessVariableType getProcessVariableTypeForPlantUnitId(
			PlantUnitId typeId) {
		ProcessVariableTypeNode node = getProcessVariableTypeNodeForPlantUnitId(typeId);
		if (node != null) {
			return node.getPlantUnit();
		}

		return null;
	}

	public IPlantUnitNode copyDeep() {
		return getRoot().copyDeep();
	}

	/**
	 * @require {@link #canAddPlantTypeNode(IPlantUnitNode)}
	 * @param node
	 */
	public void addPlantTypeNode(IPlantUnitNode node) {
		assert node != null : "assert  node != null";
		assert canAddPlantTypeNode(node) : "assert canAddPlantTypeNode(node)";

		getRoot().addChildPlantUnitNode(node);

	}

	public boolean canAddPlantTypeNode(IPlantUnitNode node) {
		assert node != null : "assert  node != null";

		return getRoot().canAddChildPlantUnitNode(node);
	}

	/**
	 * @deprecated 18.03.2011 er: entweder wir machen alles über
	 *             Factory-Methoden oder nix.
	 * @return
	 */
	public PlantSectionType createNewPlantSectionType() {
		PlantSectionType plantSectionType = new PlantSectionType(
				new PlantUnitId(), "new PlantSection "
						+ (processVariableCreationCount++));
		PlantSectionTypeNode plantSectionTypeNode = new PlantSectionTypeNode(
				new PlantNodeId(), plantSectionType);
		addPlantTypeNode(plantSectionTypeNode);

		return plantSectionType;
	}

	/**
	 * @deprecated 18.03.2011 er: entweder wir machen alles über
	 *             Factory-Methoden oder nix.
	 * @return
	 */
	public ProcessVariableType createNewProcessVariableType() {
		ProcessVariableType processVariableType = new ProcessVariableType(
				new PlantUnitId(), "new ProcessVariable "
						+ (processVariableCreationCount++));
		ProcessVariableTypeNode processVariableTypeNode = new ProcessVariableTypeNode(
				new PlantNodeId(), processVariableType);
		addPlantTypeNode(processVariableTypeNode);

		return processVariableType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		PlantInformationTypeLibrary other = (PlantInformationTypeLibrary) obj;
		if (defaultProcessVariableType == null) {
			if (other.defaultProcessVariableType != null)
				return false;
		} else if (!defaultProcessVariableType
				.equals(other.defaultProcessVariableType))
			return false;
		if (defaultType == null) {
			if (other.defaultType != null)
				return false;
		} else if (!defaultType.equals(other.defaultType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rootNode == null) {
			if (other.rootNode != null)
				return false;
		} else if (!rootNode.equals(other.rootNode))
			return false;
		return true;
	}

	public IPlantUnit updatePlantUnit(IPlantUnit alteredPlantUnit) {
		IPlantUnit plantUnit = getNodeForPlantUnitId(alteredPlantUnit.getId());

		if (plantUnit != null) {
			plantUnit.update(alteredPlantUnit);
		}
		
		return plantUnit;
	}

	public IPlantUnitNode createPlantUnitNodeFromTypeNode(
			IPlantUnitNode typeNode) {
		if (typeNode instanceof PlantSectionTypeNode) {
			return createPlantSectionNodeFromTypeNode((PlantSectionTypeNode) typeNode);

		} else if (typeNode instanceof ProcessVariableTypeNode) {
			return createProcessVariableNodeFromTypeNode((ProcessVariableTypeNode) typeNode);
		}

		return null;
	}

	private PlantSectionNode createPlantSectionNodeFromTypeNode(
			PlantSectionTypeNode typeNode) {
		PlantSection plantSection = new PlantSection(new PlantUnitId(),
				typeNode.getDisplayName(), typeNode.getPlantUnit());
		PlantSectionNode plantSectionNode = new PlantSectionNode(
				new PlantNodeId(), plantSection);

		importChildPlantUnitsFromTypeNode(typeNode, plantSectionNode);

		return plantSectionNode;
	}

	private ProcessVariableNode createProcessVariableNodeFromTypeNode(
			ProcessVariableTypeNode typeNode) {
		ProcessVariable processVariable = new ProcessVariable(
				new PlantUnitId(), typeNode.getDisplayName(),
				typeNode.getPlantUnit());
		processVariable.setValue(typeNode.getPlantUnit().getValue());
		processVariable.addAttributes(typeNode.getPlantUnit().getAttributes());

		return new ProcessVariableNode(new PlantNodeId(), processVariable);
	}

	public void importChildPlantUnitsFromOwnType(PlantSectionNode parentNode) {
		PlantSectionTypeNode plantSectionTypeNode = getPlantSectionTypeNodeForPlantUnitId(parentNode
				.getTypeId());
		importChildPlantUnitsFromTypeNode(plantSectionTypeNode, parentNode);
	}

	private void importChildPlantUnitsFromTypeNode(
			PlantSectionTypeNode sourceNode, PlantSectionNode destinationNode) {
		for (PlantSectionTemplateNode templateNode : sourceNode
				.getPlantSectionTemplateNodes()) {
			importPlantSectionFromTemplate(templateNode, destinationNode);
		}

		for (ProcessVariableTemplateNode templateNode : sourceNode
				.getProcessVariableTemplateNodes()) {
			importProcessVariableFromTemplateNode(templateNode, destinationNode);
		}
	}

	private void importPlantSectionFromTemplate(
			PlantSectionTemplateNode templateNode,
			PlantSectionNode destinationNode) {
		PlantSectionNode child = new PlantSectionNode(templateNode);

		if (destinationNode.canAddChildPlantUnitNode(child)) {
			destinationNode.addChildPlantUnitNode(child);
		}

		importChildPlantUnitsFromOwnType(child);
	}

	private void importProcessVariableFromTemplateNode(
			ProcessVariableTemplateNode templateNode,
			PlantSectionNode destinationNode) {
		ProcessVariableNode child = new ProcessVariableNode(templateNode);

		if (destinationNode.canAddChildPlantUnitNode(child)) {
			destinationNode.addChildPlantUnitNode(child);
		}
	}

	public boolean existsPlantUnit(PlantUnitId unitId) {
		return getRoot().findPlantUnitForId(unitId) != null;
	}

	public PlantInformationTypeFilter createTypeFilterIncludingAllTypes() {
		List<PlantSectionType> plantSectionTypes = new ArrayList<PlantSectionType>();
		plantSectionTypes.add(defaultType);
		plantSectionTypes.addAll(getAllPlantSectionTypes());

		return new PlantInformationTypeFilter(plantSectionTypes);
	}
}
