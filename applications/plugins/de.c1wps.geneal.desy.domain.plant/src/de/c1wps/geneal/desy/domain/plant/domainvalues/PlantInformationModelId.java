package de.c1wps.geneal.desy.domain.plant.domainvalues;

import java.io.Serializable;
import java.util.UUID;

public class PlantInformationModelId implements Serializable {

	private static final long	serialVersionUID	= -2397105948265635387L;

	private final UUID			key;

	public PlantInformationModelId() {
		this.key = UUID.randomUUID();
	}

	/**
	 * @require {@link #isValid(String)}
	 * @param uuidString
	 */
	public PlantInformationModelId(String uuidString) throws IllegalArgumentException {
		this.key = UUID.fromString(uuidString);
	}

	public String getKey() {
		return key.toString();
	}

	public static boolean isValid(String newModelId) {
		if (newModelId != null && newModelId.length() > 0) {
			return true;
		}
		return false;
	}

	public static boolean isValid(PlantInformationModelId newModelId) {
		if (newModelId != null && isValid(newModelId.getKey())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
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
		if (getKey() == null) {
			if (other.getKey() != null)
				return false;
		} else if (!getKey().equals(other.getKey()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ModelId [key=" + getKey() + "]";
	}

}
