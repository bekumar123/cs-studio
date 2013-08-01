package de.c1wps.geneal.desy.domain.plant.plantinformationservice;

import java.util.Collection;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantInformationModelViewId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModel;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModelView;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantSection;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.IPlantUnitNode;
import de.c1wps.geneal.desy.domain.plant.service.PlantInformationServiceException;

public interface IPlantInformationService {

	/**
	 * Diese Methode liefert als Plant information model das Root-Element
	 * zurueck, ueber welches das gesamte Modell abgefragt werden kann. Dieses
	 * Modell darf nur für sondierende Operationen und nicht für manipulierende
	 * Operationen!
	 * 
	 * @throws PlantInformationServiceException
	 */
	// TODO GeneAL: Diese
	// Repräsentation muss
	// nicht-manipulierbar gemacht
	// werden, um sicherzustellen,
	// dass nur sondierende
	// Operationen durchgeführt
	// werden!!!
	public PlantInformationModel getPlantInformationModel(
			PlantInformationModelId modelId);

	public PlantInformationModel getActivePlantInformationModel();

	public PlantInformationModel createNewPlantInformationModel(String name);

	public PlantInformationModel[] getAllPlantInformationModels();

	public boolean hasModel(PlantInformationModelId modelId);

	// TODO GeneAL: bessere Namen werden gerne gesehen

	/**
	 * Setzt das aktuelle Basismodell.
	 * 
	 * @param modelId
	 *            Id des Modells, das als Basismodell gesetzt werden soll.
	 */
	public void setActiveModelId(PlantInformationModelId modelId);

	public boolean hasActiveModel();

	boolean isActiveModelId(PlantInformationModelId modelId);

	/**
	 * Gibt die Id des aktuell verwendeten Basismodells zurück.
	 */
	public PlantInformationModelId getActiveModelId();

	public IPlantUnitNode getPlantNodeByReference(PlantUnitReference reference);

	public IPlantUnit getPlantUnitByReference(PlantUnitReference reference);

	boolean existsPlantUnit(PlantUnitReference reference);

	public void savePlantInformationModel(PlantInformationModel model)
			throws PlantInformationServiceException;

	public void savePlantSection(PlantSection plantSection,
			PlantInformationModelId modelId)
			throws PlantInformationServiceException;

	public void saveProcessVariable(ProcessVariable processVariable,
			PlantInformationModelId modelId)
			throws PlantInformationServiceException;

	Collection<PlantInformationModelView> getAllModelViews()
			throws PlantInformationServiceException;

	PlantInformationModelView getModelViewById(PlantInformationModelId viewId)
			throws PlantInformationServiceException;

	Collection<PlantInformationModelView> getModelViewsForActiveModel()
			throws PlantInformationServiceException;

	void saveModelView(PlantInformationModelView modelView)
			throws PlantInformationServiceException;

	void changeActiveModelView(PlantInformationModelViewId modelViewId);
}
