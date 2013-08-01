package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

public class PlantSection implements IPlantUnit, Serializable {

	private static final long serialVersionUID = -3373579794747414861L;

	final private PlantUnitId id;
	private String displayName;
	private String description;

	private PlantSectionType type;

	private String referenceCode;

	public PlantSection(PlantUnitId unitId, String displayName) {
		assert unitId != null : "unitId != null";
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";

		this.id = unitId;
		this.displayName = displayName;
		this.description = "";
		this.type = PlantSectionType.NULL;
		this.referenceCode = "";
	}

	public PlantSection(PlantUnitId unitId, String displayName,
			PlantSectionType type) {
		assert unitId != null : "unitId != null";
		assert displayName != null : "displayName != null";
		assert displayName.trim().length() > 0 : "displayName.trim().length() > 0";
		assert type != null : "type != null";

		this.id = unitId;
		this.displayName = displayName;
		this.description = "";
		this.type = type;
		this.referenceCode = "";
	}

	public PlantSection(PlantSectionTemplate template) {
		assert template != null : "template != null";

		this.id = new PlantUnitId();
		this.displayName = template.getDisplayName();
		this.description = "";
		this.type = template.getType();
		this.referenceCode = "";
	}

	public PlantUnitId getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @require {@link #hasType()}
	 * @return
	 */
	public PlantSectionType getType() {
		assert hasType() : "assert hasType()";
		return type;
	}

	public boolean hasType() {
		return type != null;
	}

	public void setType(PlantSectionType type) {
		assert type != null : "assert  type != null";

		this.type = type;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		assert referenceCode != null : "referenceCode != null";

		this.referenceCode = referenceCode;
	}

	public void update(IPlantUnit updatedPlantUnit) {
		assert getId().equals(updatedPlantUnit.getId()) : "getId().equals(updatedPlantUnit.getId())";
		assert updatedPlantUnit instanceof PlantSection : "updatedPlantUnit instanceof PlantSection";

		PlantSection updatedPlantSection = (PlantSection) updatedPlantUnit;

		setDisplayName(updatedPlantSection.getDisplayName());
		setDescription(updatedPlantSection.getDescription());
		setType(updatedPlantSection.getType());
		setReferenceCode(updatedPlantSection.getReferenceCode());
	}

	public PlantSection copyDeep() {
		PlantSection copy = new PlantSection(getId(), getDisplayName(),
				getType());
		copy.setDescription(getDescription());
		copy.setReferenceCode(getReferenceCode());

		return copy;
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

		PlantSection other = (PlantSection) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;

		if (referenceCode == null) {
			if (other.referenceCode != null)
				return false;
		} else if (!referenceCode.equals(other.referenceCode))
			return false;

		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;

		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;

		return true;
	}

	@Override
	public PlantSection createClearCopy() {
		PlantSection newPlantSection = new PlantSection(new PlantUnitId(),
				getDisplayName(), getType());
		// FIXME 16.03.2011 km, er: gehört Description zur ClearCopy?
		newPlantSection.setDescription(getDescription());

		return newPlantSection;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName() + ", Type: "
				+ getType().getDisplayName() + ", ID= " + getId();
	}
}
