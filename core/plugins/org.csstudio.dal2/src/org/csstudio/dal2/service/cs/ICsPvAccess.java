package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.service.DalException;

/**
 * Interface for the control system specific implementation of a process variable access.
 * 
 * <p><b>This class is not intended to be used by dal clients</b>. IPvAccess should be used instead.
 */
public interface ICsPvAccess<T> {

	/**
	 * Read a value from control system (async)
	 * 
	 * @param callback Callback to provide async result
	 * @return 
	 * @throws DalException
	 */
    ICsOperationHandle getValue(ICsResponseListener<CsPvData<T>> callback) throws DalException;

    /**
     * Returns true, if a monitor has been initialized for this pv access
     */
	boolean hasMonitor();
	
	/**
	 * Initializes a new monitor (listener) for this pv access
	 * 
	 * @param listener the listener to handle events
	 * 
	 * @require listener != null
	 * 
	 * @throws DalException
	 */
	void initMonitor(ICsPvListener<T> listener) throws DalException;

	/**
	 * Stops a previously initialized monitor
	 */
	void stopMonitor();

	/**
	 * The address of the pv access
	 */
	PvAddress getPvAddress();
	
}
