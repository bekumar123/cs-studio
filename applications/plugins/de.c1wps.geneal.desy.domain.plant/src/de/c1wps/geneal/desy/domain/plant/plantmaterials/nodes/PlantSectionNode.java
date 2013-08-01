package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PVConsistencyState;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSection;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

/**
 * Ist ein physisch existenter oder logisch zusammenfassender Teil einer Anlage. PlantSectionNode soll alle Baumoperationen von den fachlichen
 * Operationen einer PlantSEction kapseln.
 * 
 * @author geneAL-Team
 * 
 */
public class PlantSectionNode implements IPlantUnitNode, Serializable {

	private static final long serialVersionUID = 920080339153878511L;
	private final PlantNodeId id;

	private PlantSectionNode parent;
	private List<PlantSectionNode> childPlantSections;

	private List<ProcessVariableNode> processVariableNodes;

	private PlantSection plantSection;
	private PlantInformationModelId modelId;

	public PlantSectionNode(PlantNodeId id, PlantSection plantSection) {
		assert plantSection != null : "plantSection != null";
		assert id != null : "id != null";

		this.id = id;
		this.plantSection = plantSection;
		this.childPlantSections = new ArrayList<PlantSectionNode>();
		this.processVariableNodes = new ArrayList<ProcessVariableNode>();
	}

	public PlantSectionNode(String displayName) {
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";

		id = new PlantNodeId();
		plantSection = new PlantSection(new PlantUnitId(), displayName);
		childPlantSections = new ArrayList<PlantSectionNode>();
		processVariableNodes = new ArrayList<ProcessVariableNode>();
	}

	public PlantSectionNode(PlantSectionTemplateNode templateNode) {
		assert templateNode != null : "template != null";

		id = new PlantNodeId();
		plantSection = new PlantSection(templateNode.getPlantUnit());
		childPlantSections = new ArrayList<PlantSectionNode>();
		processVariableNodes = new ArrayList<ProcessVariableNode>();
	}

	@Override
	public PlantNodeId getId() {
		return id;
	}

	@Override
	public String getDisplayName() {
		return plantSection.getDisplayName();
	}

	@Override
	public String getReferenceCode() {
		return getPlantUnit().getReferenceCode();
	}

	/**
	 * @require {@link #hasType()}
	 */
	@Override
	public String getTypeName() {
		assert hasType() : "assert hasType()";
		return plantSection.getType().getDisplayName();
	}

	/**
	 * @require {@link #hasType()}
	 */
	public PlantUnitId getTypeId() {
		assert hasType() : "assert hasType()";
		return plantSection.getType().getId();
	}

	@Override
	public PlantUnitReference getPlantUnitReference() {
		assert hasPlantUnitReference() : "hasPlantUnitReference()";

		return new PlantUnitReference(getModelId(), getPlantUnit().getId());
	}

	@Override
	public boolean hasPlantUnitReference() {
		return getModelId() != null && getPlantUnit() != null;
	}

	public PVConsistencyState getConsistencyState() {
		PVConsistencyState result = PVConsistencyState.CONSISTENT;
		result = getConsistencyStateOfChildPlantSections(result);
		result = getConsistencyStateOfProcessVariables(result);
		return result;
	}

	private PVConsistencyState getConsistencyStateOfProcessVariables(PVConsistencyState result) {
		if (this.hasProcessVariables() && result.equals(PVConsistencyState.CONSISTENT)) {
			for (ProcessVariableNode pv : getProcessVariableNodes()) {
				PVConsistencyState state = pv.getConsistencyState();
				if (!state.equals(PVConsistencyState.CONSISTENT)) {
					result = PVConsistencyState.INCONSISTENT;
					break;
				}
			}
		}
		return result;
	}

	private PVConsistencyState getConsistencyStateOfChildPlantSections(PVConsistencyState result) {
		if (hasPlantSectionNodes()) {
			for (PlantSectionNode section : getPlantSectionNodes()) {
				PVConsistencyState state = section.getConsistencyState();
				if (!state.equals(PVConsistencyState.CONSISTENT)) {
					result = PVConsistencyState.INCONSISTENT;
					break;
				}
			}
		}
		return result;
	}

	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Liefert den parent zurück.
	 * 
	 * @required {@link #hasParent()}
	 * @return
	 */
	public PlantSectionNode getParent() {
		assert hasParent() : "hasParent()";
		return parent;
	}

	private boolean hasAncestorPlantSection(PlantSectionNode plantSection) {
		if (!hasParent()) {
			return false;
		}

		if (isMyParent(plantSection)) {
			return true;
		}

		return getParent().hasAncestorPlantSection(plantSection);
	}

	private List<PlantSectionNode> getPlantSectionNodes() {
		return new ArrayList<PlantSectionNode>(childPlantSections);
	}

	private boolean canAddPlantSectionNode(PlantSectionNode newChildPlantSection) {
		assert newChildPlantSection != null : "newChildPlantSection != null";

		return !(newChildPlantSection.equals(this) || newChildPlantSection.isMyParent(this) || hasAncestorPlantSection(newChildPlantSection));
	}

	public boolean isMyParent(IPlantUnitNode plantUnitNode) {
		if (!hasParent()) {
			return false;
		}

		return getParent().equals(plantUnitNode);
	}

	public boolean isMyAncestor(IPlantUnitNode plantUnitNode) {
		if (!hasParent())
			return false;
		if (isMyParent(plantUnitNode))
			return true;

		return getParent().isMyAncestor(plantUnitNode);
	}

	private void addPlantSectionNode(PlantSectionNode newChildPlantSection) {
		assert newChildPlantSection != null : "newChildPlantSection != null";
		assert canAddPlantSectionNode(newChildPlantSection) : "canAddChildPlantSection(newChildPlantSection)";

		newChildPlantSection.setParent(this);
	}

	public void setParent(PlantSectionNode newParent) {
		assert newParent != null : "newParent != null";
		if (hasParent()) {
			parent.internalRemoveChildNode(this);
		}
		newParent.internalAddChildNode(this);

		parent = newParent;
	}

	public IPlantUnitNode findNodeByPlantUnitId(PlantUnitId unitId) {
		if (getPlantUnit().getId().equals(unitId)) {
			return this;
		}

		for (ProcessVariableNode node : getProcessVariableNodes()) {
			if (node.getPlantUnit().getId().equals(unitId)) {
				return node;
			}
		}

		for (PlantSectionNode node : getPlantSectionNodes()) {
			IPlantUnitNode childNode = node.findNodeByPlantUnitId(unitId);
			if (childNode != null) {
				return childNode;
			}
		}

		return null;
	}

	public ProcessVariableNode findProcessVariableNodeByControlSystemAddress(String addressPart) {
		for (ProcessVariableNode processVariableNode : getProcessVariableNodes()) {
			if (processVariableNode.hasAddressPart(addressPart)) {
				return processVariableNode;
			}
		}

		for (PlantSectionNode childNode : getPlantSectionNodes()) {
			ProcessVariableNode childProcessVariableNode = childNode.findProcessVariableNodeByControlSystemAddress(addressPart);
			if (childProcessVariableNode != null) {
				return childProcessVariableNode;
			}
		}

		return null;
	}

	public List<ProcessVariableNode> getProcessVariableNodes() {
		return new ArrayList<ProcessVariableNode>(processVariableNodes);
	}

	public boolean hasProcessVariables() {
		return processVariableNodes != null && !processVariableNodes.isEmpty();
	}

	@Override
	public boolean hasChildPlantUnitNodes() {
		return hasPlantSectionNodes() || hasProcessVariables();
	}

	@Override
	public List<IPlantUnitNode> getChildPlantUnitNodes() {
		List<IPlantUnitNode> result = new ArrayList<IPlantUnitNode>();
		result.addAll(getPlantSectionNodes());
		result.addAll(getProcessVariableNodes());
		return result;
	}

	public List<IPlantUnitNode> getAllDescendantPlantUnitNodes() {
		List<IPlantUnitNode> descendants = new ArrayList<IPlantUnitNode>();

		for (PlantSectionNode child : getPlantSectionNodes()) {
			descendants.add(child);
			descendants.addAll(child.getAllDescendantPlantUnitNodes());
		}
		descendants.addAll(processVariableNodes);

		return descendants;
	}
	
	//FIXME: CME: rekursion f�gt elemente mehrfach hinzu
	public List<ProcessVariableNode> getAllDescendantProcessVariableNodes() {
		List<ProcessVariableNode> descendantPVNodes = new ArrayList<ProcessVariableNode>();

		// add this pvs
		if (hasProcessVariables()) {
			descendantPVNodes.addAll(getProcessVariableNodes());
		}

		// add child pvs
		for (PlantSectionNode child : getPlantSectionNodes()) {
//			if (child.hasProcessVariables()) {
//				descendantPVNodes.addAll(child.getProcessVariableNodes());
				descendantPVNodes.addAll(child.getAllDescendantProcessVariableNodes());
//			}

//			descendantPVNodes.addAll(child.getAllDescendantProcessVariableNodes());
		}

		return descendantPVNodes;
	}

	private boolean canAddProcessVariableNode(ProcessVariableNode processVariableNode) {
		assert processVariableNode != null : "processVariable != null";

		return !getProcessVariableNodes().contains(processVariableNode);
	}

	private void addProcessVariableNode(ProcessVariableNode processVariable) {
		assert processVariable != null : "processVariable != null";
		assert canAddProcessVariableNode(processVariable) : "canAddProcessVariable(processVariable)";

		processVariable.setParent(this);
	}

	public void addChildPlantUnitNode(IPlantUnitNode node) {
		assert canAddChildPlantUnitNode(node) : "canAddPlantUnitNode(node)";

		if (node instanceof PlantSectionNode) {
			addPlantSectionNode((PlantSectionNode) node);
		} else if (node instanceof ProcessVariableNode) {
			addProcessVariableNode((ProcessVariableNode) node);
		}

	}

	public boolean canAddChildPlantUnitNode(IPlantUnitNode node) {
		boolean result = false;

		if (node instanceof PlantSectionNode) {
			result = canAddPlantSectionNode((PlantSectionNode) node);
		} else if (node instanceof ProcessVariableNode) {
			result = canAddProcessVariableNode((ProcessVariableNode) node);
		}

		return result;
	}

	private boolean hasPlantSectionNodes() {
		return !childPlantSections.isEmpty();
	}

	public boolean hasDescendingPlantUnit(PlantUnitId unitId) {
		IPlantUnitNode unitFound = findNodeByPlantUnitId(unitId);
		return unitFound != null;
	}

	/*
	 * Irgendwann sollten wir in den SDS-Displays PlantSectionIDs hinterlegen und gegen diese prüfen, statt mit Strings zu hantieren.
	 */
	public boolean containsControlSystemAddress(String addressPart) {
		assert addressPart != null : "addressPart != null";

		// sind passendde Prozessvariblen vorhanden?
		for (ProcessVariableNode variable : processVariableNodes) {
			if (variable.hasAddressPart(addressPart)) {
				return true;
			}
		}

		// sind passende Kinder-PlantSections vorhanden?
		for (PlantSectionNode childPart : getPlantSectionNodes()) {
			if (childPart.containsControlSystemAddress(addressPart)) {
				return true;
			}
		}

		return false;
	}

	public boolean isMyPlantUnit(PlantUnitId id) {
		return getPlantUnit().getId().equals(id);
	}

	/*
	 * Nur für die Benutzung durch PlantSection.
	 */
	void internalAddChildNode(IPlantUnitNode plantUnit) {
		if (plantUnit instanceof PlantSectionNode) {
			childPlantSections.add((PlantSectionNode) plantUnit);
		} else if (plantUnit instanceof ProcessVariableNode) {
			processVariableNodes.add((ProcessVariableNode) plantUnit);
		}
	}

	/*
	 * Nur für die Benutzung durch PlantSection.
	 */
	void internalRemoveChildNode(IPlantUnitNode plantUnit) {
		if (plantUnit instanceof PlantSectionNode) {
			childPlantSections.remove(plantUnit);
		} else if (plantUnit instanceof ProcessVariableNode) {
			processVariableNodes.remove(plantUnit);
		}
	}

	public PlantSectionNode copyDeep() {
		PlantSectionNode plantSectionCopy = new PlantSectionNode(id, getPlantUnit());

		copyDeepPVs(plantSectionCopy);
		copyDeepChildPlantSections(plantSectionCopy);
		plantSectionCopy.modelId = this.modelId;

		return plantSectionCopy;
	}

	private void copyDeepChildPlantSections(PlantSectionNode plantSectionCopy) {
		for (PlantSectionNode childPlantSection : childPlantSections) {
			if (plantSectionCopy.canAddPlantSectionNode(childPlantSection)) {
				plantSectionCopy.addPlantSectionNode(childPlantSection.copyDeep());
			}
		}
	}

	private void copyDeepPVs(PlantSectionNode plantSectionCopy) {
		for (ProcessVariableNode processVariable : processVariableNodes) {
			if (plantSectionCopy.canAddProcessVariableNode(processVariable)) {
				plantSectionCopy.addProcessVariableNode(processVariable.copyDeep());
			}
		}
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

		PlantSectionNode other = (PlantSectionNode) obj;
		if (childPlantSections == null) {
			if (other.childPlantSections != null)
				return false;
		} else if (!childPlantSections.equals(other.childPlantSections))
			return false;

		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;

		if (processVariableNodes == null) {
			if (other.processVariableNodes != null)
				return false;
		} else if (!processVariableNodes.equals(other.processVariableNodes))
			return false;

		/* 15.03.2011, km, er: DO NOT compare plantSections. */

		return true;
	}

	@Override
	public String toString() {
		return "PlantSectionNode [DisplayName=" + getDisplayName() + ", Type= " + getTypeName() + ",  childPlantSections="
				+ childPlantSections.size() + ", processVariableNodes=" + processVariableNodes + "]";
	}

	/**
	 * TODO 22.02.2011, er: flagAsDuplicate ist verwirrend/unklar. Alternative überlegen.
	 * 
	 * @param flagAsDuplicate
	 * 
	 * @param id
	 * @return
	 */
	public boolean checkDuplicate(ProcessVariable processVariable) {
		boolean hasDuplicate = false;

		if (hasPlantSectionNodes()) {
			for (PlantSectionNode section : getPlantSectionNodes()) {
				boolean duplicate = section.checkDuplicate(processVariable);
				// verhindere Überschreiben von bereits auf true gesetzen
				// Wert
				if (duplicate) {
					hasDuplicate = true;
				}
			}
		}
		if (this.hasProcessVariables()) {
			for (ProcessVariableNode pv : getProcessVariableNodes()) {

				if (pv.checkDuplicate(processVariable)) {
					hasDuplicate = true;
				}
			}
		}

		return hasDuplicate;
	}

	public PlantInformationModelId getModelId() {
		if (modelId == null && hasParent()) {
			return getParent().getModelId();
		}
		return modelId;
	}

	/**
	 * @Date 22.06.2001
	 * @author er: Die Referenzen auf die Model-ID sollten wir langfristig in eine dedizierte Root-Node Klasse auslagern.
	 * 
	 * @param modelId
	 */
	public void setModelId(PlantInformationModelId modelId) {
		assert modelId != null : "modelId != null";

		this.modelId = modelId;
	}

	@Override
	public PlantSection getPlantUnit() {
		return plantSection;
	}

	public PlantSectionNode createClearCopy() {
		PlantSectionNode newNode = new PlantSectionNode(new PlantNodeId(), getPlantUnit().createClearCopy());

		if (hasPlantSectionNodes()) {
			createPlantSectionNodesClearCopy(newNode);
		}

		if (hasProcessVariables()) {
			createProcessVariableNodesClearCopy(newNode);
		}

		return newNode;
	}

	private void createPlantSectionNodesClearCopy(PlantSectionNode parent) {
		for (PlantSectionNode node : getPlantSectionNodes()) {
			PlantSectionNode clearCopy = node.createClearCopy();
			parent.addChildPlantUnitNode(clearCopy);
		}
	}

	private void createProcessVariableNodesClearCopy(PlantSectionNode newNode) {
		for (ProcessVariableNode pv : getProcessVariableNodes()) {
			ProcessVariableNode clearCopy = pv.createClearCopy();
			newNode.addChildPlantUnitNode(clearCopy);
		}
	}

	public void delete() {
		if (hasParent()) {
			getParent().internalRemoveChildNode(this);
			parent = null;
		}
	}

	/**
	 * Diese Methode sucht zu einem DisplayName eine PV.
	 * 
	 * @param aliasName
	 * @return
	 */
	public ProcessVariableNode getProcessVariableNodeByName(String displayName) {
		for (ProcessVariableNode pvNode : getProcessVariableNodes()) {
			if (pvNode.getDisplayName().equalsIgnoreCase(displayName)) {
				return pvNode;
			}
		}
		return null;
	}

	@Override
	public boolean hasType() {
		return getPlantUnit().hasType();
	}
}
