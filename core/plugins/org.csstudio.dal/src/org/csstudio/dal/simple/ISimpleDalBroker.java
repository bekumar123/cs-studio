package org.csstudio.dal.simple;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;

import com.cosylab.util.CommonException;

/**
 * 
 * @author Christian Mein
 *
 */
public interface ISimpleDalBroker {
	
	/**
	 * Utility method for JUnit testing.
	 * @return the size of properties map
	 */
	public int getPropertiesMapSize();

	/**
	 * Returns remote value.
	 * Value is read and returned synchronously.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param cparam connection parameter to remote value
	 * @return remote value
	 * @throws InstantiationException
	 * @throws CommonException
	 */
	public Object getValue(final ConnectionParameters cparam) throws InstantiationException, CommonException;

	/**
	 * Returns remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information to remote value
	 * @param type Java data type for expected remote value
	 * @return returned value already cast to requested data type, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public <T> T getValue(final RemoteInfo rinfo, final Class<T> type) throws InstantiationException, CommonException;

	/**
	 * Return remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information to remote value
	 * @return returned value, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public Object getValue(final RemoteInfo rinfo) throws InstantiationException, CommonException;

	/**
	 * Return remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param property name of remote property
	 * @return returned value, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public Object getValue(final String property) throws InstantiationException, CommonException;

	/**
	 * Asynchronously requests remote value.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param cparam complete connection parameters to remote value
	 * @param callback callback which will be notified when remote value is returned
	 * @return request object, which identifies and controls response returned to callback.
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public <T> Request<T> getValueAsync(final ConnectionParameters cparam, final ResponseListener<T> callback) throws InstantiationException, CommonException;

	/**
	 * Sends new value to remote object.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information about remote entity
	 * @param value new value to be set
	 * @throws CommonException if fails
	 * @throws InstantiationException if fails
	 */
	public void setValue(final RemoteInfo rinfo, final Object value) throws InstantiationException, CommonException;

	public <T> Request<T> setValueAsync(final ConnectionParameters cparam, final Object value, final ResponseListener<T> callback) throws Exception;

	public void registerListener(final ConnectionParameters cparam, final ChannelListener listener) throws InstantiationException, CommonException;

	public void deregisterListener(final ConnectionParameters cparam, final ChannelListener listener) throws InstantiationException, CommonException;

	public void registerListener(final ConnectionParameters cparam, final DynamicValueListener listener) throws InstantiationException, CommonException;

	public void deregisterListener(final ConnectionParameters cparam, final DynamicValueListener listener) throws InstantiationException, CommonException;

	public void registerListener(final ConnectionParameters cparam, final PropertyChangeListener listener) throws InstantiationException, CommonException;

	public void deregisterListener(final ConnectionParameters cparam, final PropertyChangeListener listener) throws InstantiationException, CommonException;

	/**
	 * Registers listener to special ExpertMonitor, which can be created by plug specific parameters.
	 *
	 * @param cparam connection parameters for remote property
	 * @param listener listener which should receive value and status updated
	 * @param paremeters plug specific parameters intended for ExpertMonitor
	 * @throws InstantiationException if fails
	 * @throws CommonException if fails
	 */
	public void registerListener(final ConnectionParameters cparam, final DynamicValueListener listener, final Map<String,Object> parameters) throws InstantiationException, CommonException;

	/**
	 * Deregisters listener to special ExpertMonitor, which was created by plug specific parameters.
	 *
	 * @param cparam connection parameters for remote property
	 * @param listener listener which should receive value and status updated
	 * @param paremeters plug specific parameters which were used to create ExpertMonitor
	 * @throws InstantiationException if fails
	 * @throws CommonException if fails
	 */
	public void deregisterListener(final ConnectionParameters cparam, final DynamicValueListener listener, final Map<String,Object> parameters) throws InstantiationException, CommonException;

	/**
	 * Return default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 *  @return default plug type
	 */
	public String getDefaultPlugType();

	/**
	 * Sets default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * So far supported values are: EPICS, TINE, Simulator.
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 * @param defautl plug type.
	 */
	public void setDefaultPlugType(final String plugType);

	public void releaseAll();

}
