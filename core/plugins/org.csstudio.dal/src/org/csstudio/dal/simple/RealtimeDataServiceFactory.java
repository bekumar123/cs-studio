package org.csstudio.dal.simple;

import org.csstudio.dal.CssApplicationContext;

/**
 * 
 * @author Christian Mein
 *
 */
public class RealtimeDataServiceFactory implements IRealtimeDataServiceFactory {

	public ISimpleDalBroker getNewDataService(CssApplicationContext cssApplicationContext) {
		return SimpleDALBroker.newInstance(cssApplicationContext);
	}
}
