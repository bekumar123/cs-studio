package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

public class ProcessVariableTemplate implements IPlantUnit, Serializable {

	private static final long	serialVersionUID	= -6509152773931924941L;
	private final PlantUnitId	id;
	private String				displayName;
	private String				description;
	private ProcessVariableType	type;

	public ProcessVariableTemplate(PlantUnitId id, String displayName, ProcessVariableType type) {
		assert id != null : "id != null";
		assert displayName != null : "displayName != null";
		assert type != null : "type != null";

		this.id = id;
		this.displayName = displayName;
		this.description = "";
		this.type = type;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String newDisplayName) {
		assert newDisplayName != null : "newDisplayName != null";

		displayName = newDisplayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String newDescription) {
		assert newDescription != null : "newDescription != null";

		description = newDescription;
	}

	@Override
	public PlantUnitId getId() {
		return id;
	}

	public ProcessVariableType getType() {
		return type;
	}

	public void setType(ProcessVariableType newType) {
		assert newType != null : "newType != null";

		type = newType;
	}

	@Override
	public ProcessVariableTemplate copyDeep() {
		ProcessVariableTemplate copy = new ProcessVariableTemplate(getId(), getDisplayName(), getType());

		return copy;
	}

	@Override
	public void update(IPlantUnit unit) {
		assert unit != null : "unit != null";
		assert unit instanceof ProcessVariableTemplate : "unit instanceof ProcessVariableTemplate";

		ProcessVariableTemplate template = (ProcessVariableTemplate) unit;

		setDisplayName(template.getDisplayName());
		setDescription(template.getDescription());
		setType(template.getType());
	}

	@Override
	public ProcessVariableTemplate createClearCopy() {
		ProcessVariableTemplate clearCopy = new ProcessVariableTemplate(new PlantUnitId(), getDisplayName(), getType()
				.createClearCopy());
		return clearCopy;
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
		ProcessVariableTemplate other = (ProcessVariableTemplate) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName() + ", Type=" + getType().getDisplayName();
	}
}
