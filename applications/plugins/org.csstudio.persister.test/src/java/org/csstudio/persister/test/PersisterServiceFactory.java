package org.csstudio.persister.test;

import org.csstudio.persister.declaration.IPersistenceService;
import org.csstudio.persister.internal.PersistenceService;

/**
 * Factory to create PersistenceServices in other test fragments
 */
public class PersisterServiceFactory {

	public static IPersistenceService createService() {
		return new PersistenceService();
	}
	
}
