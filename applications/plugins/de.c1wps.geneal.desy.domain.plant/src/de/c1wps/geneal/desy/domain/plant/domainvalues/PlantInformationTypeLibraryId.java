package de.c1wps.geneal.desy.domain.plant.domainvalues;

import java.io.Serializable;
import java.util.UUID;

public class PlantInformationTypeLibraryId implements Serializable {
	private static final long	serialVersionUID	= 6993887547412235439L;

	private final UUID			id;

	/**
	 * @param uuid
	 */
	public PlantInformationTypeLibraryId(UUID uuid) {
		assert uuid != null : "uuid != null";
		this.id = uuid;
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
		PlantInformationModelId other = (PlantInformationModelId) obj;
		if (id == null) {
			if (other.getKey() != null)
				return false;
		} else if (!id.equals(other.getKey()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TypeLibraryId [" + id.toString() + "]";
	}
}
