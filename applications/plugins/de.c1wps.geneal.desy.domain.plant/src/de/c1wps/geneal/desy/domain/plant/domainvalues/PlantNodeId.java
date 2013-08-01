package de.c1wps.geneal.desy.domain.plant.domainvalues;

import java.io.Serializable;
import java.util.UUID;

public class PlantNodeId implements Serializable {

	private static final long serialVersionUID = -8565336052097196657L;
	private UUID id;

	public PlantNodeId(UUID id) {
		this.id = id;
	}

	public PlantNodeId() {
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
		PlantNodeId other = (PlantNodeId) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PlantNodeId [id=" + id + "]";
	}
}
