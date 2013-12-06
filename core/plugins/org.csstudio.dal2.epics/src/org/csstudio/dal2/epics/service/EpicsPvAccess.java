package org.csstudio.dal2.epics.service;

import gov.aps.jca.Context;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;

/**
 * Epics specific implementation of an {@link ICsPvAccess}
 * 
 * @param <T>
 */
public class EpicsPvAccess<T> implements ICsPvAccess<T> {

	/**
	 * The JCA contect
	 */
	private final Context _jcaContext;

	/**
	 * Used addess
	 */
	private final PvAddress _pv;

	/**
	 * The current channel monitor or null
	 */
	private ChannelMonitor<T> _monitor;

	/**
	 * The type used for the connection to the process variable
	 */
	private Type<T> _type;
	
	/**
	 * A map with open async requests
	 */
	private Map<ICsResponseListener<CsPvData<T>>, AbstractChannelOperator> openRequests = new HashMap<ICsResponseListener<CsPvData<T>>, AbstractChannelOperator>();

	/**
	 * Constructor
	 * 
	 * @param jcaContext
	 *            the JCA Context
	 * @param pv
	 *            the address of the process variable to be connected
	 * @param type
	 *            the type used for the connection to the process variable
	 * 
	 * @require jcaContext != null
	 * @require pv != null
	 * @require type != null
	 */
	public EpicsPvAccess(final Context jcaContext, PvAddress pv, Type<T> type) {

		assert jcaContext != null : "Precondition: jcaContext!=null";
		assert pv != null : "Precondition: pv != null";
		assert type != null : "Precondition: type != null";

		_jcaContext = jcaContext;
		_pv = pv;
		_type = type;
	}

	@Override
	public final PvAddress getPvAddress() {
		return _pv;
	}

	@Override
	public synchronized boolean hasMonitor() {
		return _monitor != null;
	}

	@Override
	public synchronized void initMonitor(ICsPvListener<T> listener)
			throws DalException {

		if (listener == null) {
			throw new IllegalArgumentException("listener must noch be null");
		}

		if (hasMonitor()) {
			if (!listener.equals(_monitor.getListener())) {
				throw new IllegalStateException(
						"Monitor already initialized with a different listener");
			}
		} else {
			_monitor = new ChannelMonitor<T>(_jcaContext, _pv, _type, listener);
		}

	}

	@Override
	public synchronized void stopMonitor() {
		if (!hasMonitor()) {
			throw new IllegalStateException("Monitor not initialized");
		}

		_monitor.dispose();
		_monitor = null;
	}

	@Override
	public ICsOperationHandle getValue(final ICsResponseListener<CsPvData<T>> callback)
			throws DalException {

		GetValueRequester<T> requester = new GetValueRequester<T>(_jcaContext, _pv,
						_type, callback) {
					@Override
					protected void onDispose() {
						openRequests.remove(callback);
					}
				};
		openRequests.put(callback, requester);
		assert requester != null : "Postcondition: result != null";
		return requester;
	}
	
	protected final Context getJcaContext() {
		return _jcaContext;
	}

}
