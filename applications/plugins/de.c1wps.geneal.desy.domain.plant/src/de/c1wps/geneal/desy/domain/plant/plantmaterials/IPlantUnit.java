package de.c1wps.geneal.desy.domain.plant.plantmaterials;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;

/**
 * 
 */
public interface IPlantUnit {

	public String getDisplayName();

	public void setDisplayName(String displayName);

	public String getDescription();

	public void setDescription(String description);

	public IPlantUnit copyDeep();

	public PlantUnitId getId();

	public void update(IPlantUnit unit);

	public IPlantUnit createClearCopy();
}
