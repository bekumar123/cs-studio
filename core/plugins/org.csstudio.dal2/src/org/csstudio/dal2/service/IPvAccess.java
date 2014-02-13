package org.csstudio.dal2.service;

import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;


/**
 * The IPvAccess is the central interface for accessing process variables (PVs) in the underlying control system (CS).
 * <p>
 * It allows to read, (write) and monitor PVs. 
 * <p>
 * The IPvAccess is based on a single {@link Type} to connect the CS.
 *
 * @param <T> The java type of the used {@link Type}.
 */
public interface IPvAccess<T> {

	 /**
     * Register listener for the given pv. More than one listener may be registered on the same pv.
     * You may not register the same listener more than once.
     * @param pv
     * @param listener
     * @throws DalException
     */
    void registerListener(IPvListener<T> listener) throws DalException;

    /**
     * Deregister listener. If the listener has not been registered, nothing happens.
     * @param listener
     * @throws DalException
     */
    void deregisterListener(IPvListener<T> listener) throws DalException;

    /**
     * Deregister all listener.
     */
    void deregisterAllListener();

    /**
	 * Fetches the current value in an asynchronous operation using the default timeout. The result ist provided to the given callback
	 * 
	 * @param callback callback object to handle the result
	 * 
	 * @throws DalException
	 */
	void getValue(IResponseListener<T> callback) throws DalException;
    
    /**
	 * Fetches the current value in an asynchronous operation. The result is provided to the given callback
	 * 
	 * @param callback callback object to handle the result
	 * 
	 * @throws DalException
	 */
	void getValue(long timeout, TimeUnit unit, IResponseListener<T> callback) throws DalException;
    
	/**
	 * Fetches the current value in a synchronous (blocking) operation using the default timeout
	 * 
	 * @return the current value
	 * 
	 * @throws DalException
	 */
    T getValue() throws DalException;

    /**
	 * Fetches the current value in a synchronous (blocking) operation using a given timeout
	 * 
	 * @param timeout timeout in the given unit
	 * @param unit the time unit of the timeout 
	 * @return the current value
	 * 
	 * @throws DalException
	 */
	T getValue(long timeout, TimeUnit unit) throws DalException;
    
    /**
     * Provides the used address
     * 
     * @return the address
     * @ensure result != null
     */
	PvAddress getPVAddress();

	/**
	 * Provides the used type to access the control system
	 * 
	 * @return the type
	 * @ensure result != null
	 */
	Type<T> getType();

	/**
	 * Provides the used listener type to access the control system
	 * 
	 * @return the listener type
	 * @ensure result != null
	 */
	ListenerType getListenerType();
	
	/**
	 * Provides the last known characteristics or <b>null</b>, if characteristics are not available 
	 */
	Characteristics getLastKnownCharacteristics();

	/**
	 * Provides the last known value or <b>null</b>, if no value is available
	 */
	T getLastKnownValue();

	/**
	 * Returns true if a (last known) value is available
	 */
	boolean hasLastKnownValue();
	
	/**
	 * Returns true, if the pv access is connected. (Only a monitor will create a durable connection.)  
	 */
	boolean isConnected();

	/**
	 * Returns true if a (last known) native type is available
	 */
	boolean hasLastKnownNativeType();
	
	/**
	 * Returns the last known native type of the pv.
	 */
	Type<?> getLastKnownNativeType();

}