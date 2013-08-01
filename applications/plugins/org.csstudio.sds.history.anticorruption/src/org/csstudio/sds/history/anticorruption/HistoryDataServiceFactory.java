package org.csstudio.sds.history.anticorruption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.dal.simple.ISimpleDalBroker;
import org.csstudio.sds.history.IHistoryDataService;
import org.csstudio.sds.history.anticorruption.service.IHistoryDataServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.c1wps.geneal.desy.domain.plant.plantinformationservice.IPlantInformationService;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantInformationModel;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.ProcessVariable;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.PlantSectionNode;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes.ProcessVariableNode;

public class HistoryDataServiceFactory implements IHistoryDataServiceFactory {

	private static Logger LOG = LoggerFactory.getLogger(HistoryDataServiceFactory.class);
	
	private IHistoryDataService _historyDataService;
	
	private IPlantInformationService _plantInformationService;

	private volatile Map<String, ProcessVariable> _allProcessVariables = new HashMap<String, ProcessVariable>();
	
	@Override
	public ISimpleDalBroker getDataService() {
		ISimpleDalBroker simpleDalBroker = new DalBrokerAntiCorruptionLayer(_allProcessVariables, _historyDataService);
		
		return simpleDalBroker;
	}
	
	private void storeAllProcessVariables(IPlantInformationService plantInfService) {
		List<ProcessVariableNode> allProcessVariableNodes = new ArrayList<ProcessVariableNode>();

		// get and store a list of all process variables from the plantInformationService
		PlantInformationModel[] allPlantInformationModels = plantInfService.getAllPlantInformationModels();
		// TODO: maybe the PlantInformationmodel should manage a list of all available process-variables.
		for (PlantInformationModel plantInformationModel : allPlantInformationModels) {
			PlantSectionNode rootPlantSectionNode = plantInformationModel.getRoot();
			allProcessVariableNodes.addAll(rootPlantSectionNode.getAllDescendantProcessVariableNodes());
		}

		for (ProcessVariableNode processVariableNode : allProcessVariableNodes) {
			ProcessVariable pv = processVariableNode.getPlantUnit();
			_allProcessVariables.put(pv.getControlSystemAddress(), pv);
		}
	}
	
	
	public void bindPlantInformatioService(IPlantInformationService plantInformationService) {
		_plantInformationService = plantInformationService;
		storeAllProcessVariables(_plantInformationService);
	}
	
	public void unbindPlantInformatioService(IPlantInformationService plantInformationService) {
		_plantInformationService = null;
		_allProcessVariables = new HashMap<String, ProcessVariable>();
	}
	
	public void bindHistoryDataService(IHistoryDataService historyDataSerice) {
		LOG.info("bindHistoryDataService");
		_historyDataService = historyDataSerice;
	}
	
	public void unbindHistoryDataService(IHistoryDataService historyDataSerice) {
		_historyDataService = null;
	}
}