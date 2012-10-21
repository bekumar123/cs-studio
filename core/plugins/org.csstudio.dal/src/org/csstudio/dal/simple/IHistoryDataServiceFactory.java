package org.csstudio.dal.simple;

import de.c1wps.geneal.desy.domain.plant.plantinformationservice.IPlantInformationService;

public interface IHistoryDataServiceFactory extends IDataServiceFactory {
	
	
	public void bindPlantInformationService(IPlantInformationService plantInfService);
	
	public void unbindPlantInformationService();	

}
