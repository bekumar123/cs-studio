package org.csstudio.ams.distributor.service;

import java.sql.Connection;
import java.util.Map;

import org.csstudio.ams.dbAccess.configdb.MessageExtensionsDAO;

public class MessageExtensionDbService implements MessageExtensionService {

	private Map<String, Map<String, String>> messageExtensions;
	private final Connection databaseConnection;
	
	public MessageExtensionDbService(Connection databaseConnection) {
		this.databaseConnection = databaseConnection;
		reloadConfiguration();
	}



	@Override
	public Map<String, String> getMessageExtensions(String pv) {
		assert hasMessageExtensions(pv) : "hasMessageExtensions(pv)";
		
		return messageExtensions.get(pv);
	}
	
	

	@Override
	public void reloadConfiguration() {
		messageExtensions = MessageExtensionsDAO.loadAllMessageExtensions(databaseConnection);
	}

	

	@Override
	public boolean hasMessageExtensions(String pv) {
		return messageExtensions.containsKey(pv);
	}

}
