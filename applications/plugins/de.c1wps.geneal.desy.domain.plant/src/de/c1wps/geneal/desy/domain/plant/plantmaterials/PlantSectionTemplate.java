package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

public class PlantSectionTemplate implements IPlantUnit, Serializable {

	private static final long	serialVersionUID	= 5731651429056422315L;
	private PlantUnitId			id;
	private String				displayName;
	private PlantSectionType	type;
	private String				description			= "";

	public PlantSectionTemplate(PlantUnitId id, String displayName, PlantSectionType type) {
		assert id != null : "assert  id != null";
		assert displayName != null : "assert  displayName != null";
		assert type != null : "type != null";

		this.id = id;
		this.displayName = displayName;
		this.description = "";
		this.type = type;
	}

	public void setType(PlantSectionType type) {
		assert type != null : "assert  type != null";

		this.type = type;
	}

	public PlantSectionType getType() {
		return type;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public PlantUnitId getId() {
		return id;
	}

	public void setDisplayName(String displayName) {
		assert displayName != null : "displayName != null";
		this.displayName = displayName;
	}

	@Override
	public PlantSectionTemplate copyDeep() {
		PlantSectionTemplate template = new PlantSectionTemplate(getId(), getDisplayName(), getType());
		return template;
	}

	@Override
	public void update(IPlantUnit unit) {
		assert unit != null : "assert  unit != null";
		assert unit instanceof PlantSectionTemplate : "assert unit instanceof PlantSectionTemplate";

		PlantSectionTemplate template = (PlantSectionTemplate) unit;

		setDisplayName(template.getDisplayName());
		setDescription(template.getDescription());
		setType(template.getType());
	}

	@Override
	public PlantSectionTemplate createClearCopy() {
		PlantSectionTemplate template = new PlantSectionTemplate(new PlantUnitId(), getDisplayName(), getType());
		return template;
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
		PlantSectionTemplate other = (PlantSectionTemplate) obj;
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
