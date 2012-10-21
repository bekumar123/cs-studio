package org.csstudio.dal.simple;

import org.csstudio.dal.CssApplicationContext;

/**
 * 
 * @author Christian Mein
 *
 */
public class RealtimeDataServiceFactory implements IRealtimeDataServiceFactory {

	public SimpleDALBroker getNewDataService(CssApplicationContext cssApplicationContext) {
		// TODO: CME: ...
		return SimpleDALBroker.newInstance(cssApplicationContext);
	}
	
//	public SimpleDALBroker getNewDataService
}
