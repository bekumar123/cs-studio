package org.csstudio.sds.model;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

public class PlantUnitReferenceContainer {

	private PlantUnitReference reference;

	public PlantUnitReferenceContainer() {
	}

	public void setPlantUnitReference(PlantUnitReference reference) {
		this.reference = reference;
	}

	public PlantUnitReference getPlantUnitReference() {
		return reference;
	}

	public void clear() {
		reference = null;
	}

	public boolean isEmpty() {
		return reference == null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((reference == null) ? 0 : reference.hashCode());
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
		PlantUnitReferenceContainer other = (PlantUnitReferenceContainer) obj;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		return true;
	}

}
