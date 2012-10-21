package org.csstudio.dal.simple;

import org.csstudio.dal.CssApplicationContext;

/**
 * 
 * @author Christian Mein
 *
 */
public interface IDataServiceFactory {
	
	public ISimpleDalBroker getNewDataService(CssApplicationContext cssApplicationContext);
}
