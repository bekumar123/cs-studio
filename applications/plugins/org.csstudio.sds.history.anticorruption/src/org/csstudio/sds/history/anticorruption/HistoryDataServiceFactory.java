package org.csstudio.sds.history.anticorruption;

import org.csstudio.dal.simple.ISimpleDalBroker;
import org.csstudio.sds.history.IHistoryDataService;
import org.csstudio.sds.history.anticorruption.service.IHistoryDataServiceFactory;
import org.csstudio.sds.history.domain.service.IPvInformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryDataServiceFactory implements IHistoryDataServiceFactory {

	private static Logger LOG = LoggerFactory.getLogger(HistoryDataServiceFactory.class);
	
	private IHistoryDataService _historyDataService;
	
	private IPvInformationService _pvInformationService;
	
	@Override
	public ISimpleDalBroker getDataService() {
		ISimpleDalBroker simpleDalBroker = new DalBrokerAntiCorruptionLayer(_pvInformationService, _historyDataService);
		
		return simpleDalBroker;
	}
	
	public void bindHistoryDataService(IHistoryDataService historyDataSerice) {
		LOG.info("bindHistoryDataService");
		_historyDataService = historyDataSerice;
	}
	
	public void unbindHistoryDataService(IHistoryDataService historyDataSerice) {
		_historyDataService = null;
	}
	
	public void bindPvInformationService(IPvInformationService pvInformationService) {
		_pvInformationService = pvInformationService;
	}
	
	public void unbindPvInformationService(IPvInformationService pvInformationService) {
		_pvInformationService = null;
	}
}