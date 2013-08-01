package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.io.Serializable;
import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PVConsistencyState;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;

public class ProcessVariableNode implements IPlantUnitNode, Serializable {

	private static final long serialVersionUID = 7824857110009841912L;

	final private PlantNodeId id;

	private PlantSectionNode parent;

	private ProcessVariable processVariable;

	public ProcessVariableNode(PlantNodeId id, ProcessVariable processVariable) {
		assert id != null : "id != null";
		assert processVariable != null : "processVariable != null";

		this.id = id;
		this.processVariable = processVariable;
	}

	public ProcessVariableNode(String displayName) {
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";

		id = new PlantNodeId();
		processVariable = new ProcessVariable(new PlantUnitId(), displayName);
	}

	public ProcessVariableNode(ProcessVariableTemplateNode templateNode) {
		assert templateNode != null : "template != null";

		id = new PlantNodeId();
		processVariable = new ProcessVariable(templateNode.getPlantUnit());
	}

	public PlantNodeId getId() {
		return this.id;
	}

	public String getDisplayName() {
		return getPlantUnit().getDisplayName();
	}

	@Override
	public String getReferenceCode() {
		return getPlantUnit().getReferenceCode();
	}

	@Override
	public String getTypeName() {
		assert hasType() : "assert hasType()";
		return getPlantUnit().getType().getDisplayName();
	}

	@Override
	public PlantUnitId getTypeId() {
		assert hasType() : "assert hasType()";
		return getPlantUnit().getType().getId();
	}

	public boolean hasType() {
		return getPlantUnit().hasType();
	}

	public String getControlSystemAddress() {
		return getPlantUnit().getControlSystemAddress();
	}

	public PlantInformationModelId getModelId() {
		if (hasParent()) {
			return getParent().getModelId();
		}
		return null;
	}

	/**
	 * @Deprecated 16.03.2011 er: wir sollten so schnell wie möglich von diesen
	 *             Strings weg und mit {@link PlantUnitId} arbeiten.
	 * 
	 */
	public boolean hasAddressPart(String addressPart) {
		return getPlantUnit().hasAddressPart(addressPart);
	}

	public boolean checkDuplicate(ProcessVariable otherPv) {
		return getPlantUnit().checkDuplicate(otherPv);
	}

	public PlantSectionNode getParent() {
		return parent;
	}

	public PVConsistencyState getConsistencyState() {
		return getPlantUnit().getConsistencyState();
	}

	void setParent(PlantSectionNode newParent) {
		assert newParent != null : "newParent != null";
		if (hasParent()) {
			parent.internalRemoveChildNode(this);
		}
		newParent.internalAddChildNode(this);

		parent = newParent;
	}

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
	public ProcessVariable getPlantUnit() {
		return processVariable;
	}

	@Override
	public PlantUnitReference getPlantUnitReference() {
		return new PlantUnitReference(getModelId(), getPlantUnit().getId());
	}

	@Override
	public boolean hasPlantUnitReference() {
		return getModelId() != null && getPlantUnit() != null;
	}

	private boolean isMyParent(IPlantUnitNode plantUnitNode) {
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

	/*
	 * Eine PV ist das atomare Element und kann daher keine Kinder aufnehmen.
	 */
	@Override
	public boolean canAddChildPlantUnitNode(IPlantUnitNode node) {
		return false;
	}

	@Override
	public void addChildPlantUnitNode(IPlantUnitNode node) {
		assert canAddChildPlantUnitNode(node) : "canAddPlantUnitNode(node)";

		// do nothing
	}

	@Override
	public void delete() {
		if (hasParent()) {
			getParent().internalRemoveChildNode(this);
			parent = null;
		}
	}

	@Override
	public ProcessVariableNode createClearCopy() {
		ProcessVariableNode clearCopy = new ProcessVariableNode(
				new PlantNodeId(), getPlantUnit().createClearCopy());

		return clearCopy;
	}

	public ProcessVariableNode copyDeep() {
		ProcessVariableNode processVariableCopy = new ProcessVariableNode(
				getId(), getPlantUnit().copyDeep());
		return processVariableCopy;
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

		ProcessVariableNode other = (ProcessVariableNode) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		return true;
	}

	@Override
	public String toString() {
		String type = ", Type=";
		if (hasType()) {
			type += getTypeName();
		}
		return "ProcessVariableNode [name=" + getDisplayName() + type + " ]";
	}

}
