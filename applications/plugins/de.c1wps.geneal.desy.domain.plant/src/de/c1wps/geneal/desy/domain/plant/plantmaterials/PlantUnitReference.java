package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import java.io.Serializable;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

/**
 * Ordnet ein Anlagenteil eindeutig einem Modell zu.
 * 
 * Hat den Zweck, dass man ein Anlagenteil schneller finden kann, und nicht in
 * allen Modellen suchen muss.
 * 
 * @author cz, er
 */
public class PlantUnitReference implements Serializable {
	private static final long				serialVersionUID	= -7908112046473321331L;

	public static final PlantUnitReference	NULL				= new PlantUnitReferenceNull();

	private PlantInformationModelId			modelId;
	private PlantUnitId						unitId;

	public PlantUnitReference(PlantInformationModelId modelId, PlantUnitId unitId) {
		assert modelId != null : "modelId != null";
		assert unitId != null : "unitId != null";

		this.modelId = modelId;
		this.unitId = unitId;
	}

	public PlantInformationModelId getModelId() {
		return modelId;
	}

	public PlantUnitId getUnitId() {
		return unitId;
	}

	public boolean containsUnitId(PlantUnitId id) {
		assert id != null : "id != null";

		return unitId.equals(id);
	}

	public boolean containsModelId(PlantInformationModelId id) {
		assert id != null : "id != null";

		return modelId.equals(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
		result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
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
		PlantUnitReference other = (PlantUnitReference) obj;
		if (modelId == null) {
			if (other.modelId != null)
				return false;
		} else if (!modelId.equals(other.modelId))
			return false;
		if (unitId == null) {
			if (other.unitId != null)
				return false;
		} else if (!unitId.equals(other.unitId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + modelId.toString() + ", " + unitId.toString() + ")";
	}

	private static class PlantUnitReferenceNull extends PlantUnitReference {

		private static final long	serialVersionUID	= 4935701301253488671L;

		private PlantUnitReferenceNull() {
			super(new PlantInformationModelId(), new PlantUnitId());
		}

		@Override
		public boolean containsModelId(PlantInformationModelId id) {
			return false;
		}

		@Override
		public boolean containsUnitId(PlantUnitId id) {
			return false;
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

		@Override
		public String toString() {
			return getClass().getName();
		}
	}

}
