package org.csstudio.dal.simple;

import org.csstudio.dal.CssApplicationContext;

/**
 * 
 * @author Christian Mein
 *
 */
public class RealtimeDataServiceFactory implements IRealtimeDataServiceFactory {

	@Override 
	public ISimpleDalBroker getDataService(CssApplicationContext cssApplicationContext) {
		return SimpleDALBroker.newInstance(cssApplicationContext);
	}
}
