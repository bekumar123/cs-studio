package org.csstudio.dal2.service;

/**
 * A Factory providing a new instance of an IDalService.
 * <p>
 * Clients should use only one instance of the IDalService. 
 */
public interface IDalServiceFactory {

	IDalService newDalService();

}
