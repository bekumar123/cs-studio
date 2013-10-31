package org.csstudio.ams.distributor.service;

import java.util.Map;

public interface MessageExtensionService {

	Map<String, String> getMessageExtensions(String pv);
	
	boolean hasMessageExtensions(String pv);
	
	void reloadConfiguration();
}
