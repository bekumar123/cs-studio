package org.csstudio.sds.history.anticorruption.service;

import org.csstudio.dal.simple.ISimpleDalBroker;

public interface IHistoryDataServiceFactory {
	
	public ISimpleDalBroker getDataService();
	
}
