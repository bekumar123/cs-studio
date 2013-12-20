package org.csstudio.dal.simple;

import org.csstudio.dal.CssApplicationContext;

public interface IRealtimeDataServiceFactory {
	
	public ISimpleDalBroker getDataService(CssApplicationContext cssApplicationContext);

}
