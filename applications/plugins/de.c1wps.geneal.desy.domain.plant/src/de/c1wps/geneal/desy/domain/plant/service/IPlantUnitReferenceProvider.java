package de.c1wps.geneal.desy.domain.plant.service;

import java.util.List;

import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

public interface IPlantUnitReferenceProvider {

	public List<PlantUnitReference> getAllReferences();

	public void addUpdateListener(IPlantUnitReferenceUpdateListener listener);

}
