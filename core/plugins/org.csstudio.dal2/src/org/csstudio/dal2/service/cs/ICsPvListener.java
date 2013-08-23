package org.csstudio.dal2.service.cs;

import org.csstudio.dal2.dv.ListenerType;

/**
 * A pv listener
 * <p>
 * <i>This is part of the interface within DAL2 between generic and the control system specific part</i>  
 *
 * @param <T>
 */
public interface ICsPvListener<T> {

	/**
	 * Called when the connection state changed
	 * 
	 * @param pvName Name of the pv
	 * @param isConnected
	 */
    void connectionChanged(String pvName, boolean isConnected);

    /**
     * Called when the state of the pv has changed (according to the listener type: {@link #getType()})
     * 
     * @param value a CsPvData object describing the current value of the pv
     */
    void valueChanged(CsPvData<T> value);
	
    /**
     * Called when an error occured
     */
	void errorReceived(String message);
	
	/**
	 * Provides the type of the listener
	 */
	ListenerType getType();
}
