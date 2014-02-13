package org.csstudio.dal2.service.test;

import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.cs.ICsPvAccessFactory;
import org.csstudio.dal2.service.impl.DalService;

/**
 * Utility class to create dal service instances in other junit tests
 */
public class DalServiceTestUtil {

	public static IDalService createService(ICsPvAccessFactory pluginService) {
		
		return new DalService(pluginService);
	}
	
}
