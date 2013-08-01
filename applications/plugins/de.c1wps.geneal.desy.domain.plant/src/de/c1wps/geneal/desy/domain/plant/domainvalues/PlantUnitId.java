package de.c1wps.geneal.desy.domain.plant.domainvalues;

import java.io.Serializable;
import java.util.UUID;

public class PlantUnitId implements Serializable {

	private static final long	serialVersionUID	= -8586186128772959314L;

	private UUID				id;

	public PlantUnitId(UUID id) {
		this.id = id;
	}

	public PlantUnitId(String uuidString) throws IllegalArgumentException {
		this.id = UUID.fromString(uuidString);
	}

	public PlantUnitId() {
		this(UUID.randomUUID());
	}

	public String getKey() {
		return id.toString();
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
		PlantUnitId other = (PlantUnitId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlantUnitId [id=" + id + "]";
	}
}
