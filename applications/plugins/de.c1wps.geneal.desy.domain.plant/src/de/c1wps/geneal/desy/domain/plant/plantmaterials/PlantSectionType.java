package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

public class PlantSectionType implements IPlantUnitType, Serializable {

	private static final long					serialVersionUID	= 2250711955336547912L;

	public static final PlantSectionTypeNull	NULL				= new PlantSectionTypeNull();

	private PlantUnitId							id;
	private String								displayName;
	private String								description;

	public PlantSectionType(PlantUnitId id, String displayName) {
		assert id != null : "id != null";
		assert displayName != null : "displayName != null";

		this.id = id;
		this.displayName = displayName;
		this.description = "";
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
	public PlantSectionType copyDeep() {
		PlantSectionType copy = new PlantSectionType(getId(), getDisplayName());
		copy.setDescription(getDescription());

		return copy;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public PlantUnitId getId() {
		return id;
	}

	@Override
	public void update(IPlantUnit unit) {
		assert unit != null : "unit != null";
		assert unit instanceof PlantSectionType : "unit instanceof PlantSectionType";

		setDisplayName(unit.getDisplayName());
		setDescription(unit.getDescription());
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public PlantSectionType createClearCopy() {
		PlantSectionType clearCopy = new PlantSectionType(new PlantUnitId(), getDisplayName() + " Copy");
		clearCopy.setDescription(getDescription());

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
		PlantSectionType other = (PlantSectionType) obj;
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
		return true;
	}

	@Override
	public String toString() {
		return "DisplayName=" + getDisplayName();
	}

	/**
	 * NullPattern für PlantSectionType
	 * 
	 * @author reiswich
	 * 
	 */
	private static class PlantSectionTypeNull extends PlantSectionType implements Serializable {

		private static final long	serialVersionUID	= 7781207518100037775L;

		public PlantSectionTypeNull() {
			super(new PlantUnitId(), "undefined");
		}

		@Override
		public void setDisplayName(String displayName) {
			// do nothing
		}

		@Override
		public void setDescription(String description) {
			// do nothing
		}

		@Override
		public void update(IPlantUnit unit) {
			// do nothing
		}

		@Override
		public PlantSectionType createClearCopy() {
			return this;
		}

		@Override
		public PlantSectionType copyDeep() {
			return this;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}

			return o.getClass().equals(this.getClass());
		}
	}

}
