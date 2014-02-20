package org.csstudio.dal2.epics.service;

import gov.aps.jca.Context;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal2.dv.ConnectionState;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.epics.mapping.IEpicsTypeMapping;
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
	private Map<ICsResponseListener<CsPvData<T>>, AbstractChannelOperator> _openRequests = new HashMap<ICsResponseListener<CsPvData<T>>, AbstractChannelOperator>();

	private IEpicsTypeMapping _typeMapping;

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
	 * @require typeMapping != null
	 * @require pv != null
	 * @require type != null
	 */
	public EpicsPvAccess(final Context jcaContext,
			IEpicsTypeMapping typeMapping, PvAddress pv, Type<T> type) {

		assert jcaContext != null : "Precondition: jcaContext!=null";
		assert typeMapping != null : "Precondition: typeMapping != null";
		assert pv != null : "Precondition: pv != null";
		assert type != null : "Precondition: type != null";

		_jcaContext = jcaContext;
		_typeMapping = typeMapping;
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
			_monitor = new ChannelMonitor<T>(_jcaContext, _typeMapping, _pv,
					_type, listener);
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
	public ConnectionState getConnectionState() {
		if (_monitor == null) {
			return ConnectionState.NOT_REQUESTED;
		} else {
			gov.aps.jca.Channel.ConnectionState state = _monitor.getChannel()
					.getConnectionState();
			if (gov.aps.jca.Channel.ConnectionState.NEVER_CONNECTED
					.equals(state)) {
				return ConnectionState.NEVER_CONNECTED;
			} else if (gov.aps.jca.Channel.ConnectionState.CLOSED.equals(state)) {
				return ConnectionState.CLOSED;
			} else if (gov.aps.jca.Channel.ConnectionState.CONNECTED
					.equals(state)) {
				return ConnectionState.CONNECTED;
			} else if (gov.aps.jca.Channel.ConnectionState.DISCONNECTED
					.equals(state)) {
				return ConnectionState.DISCONNECTED;
			} else {
				return ConnectionState.UNDEFINED;
			}
		}
	}

	@Override
	public ICsOperationHandle getValue(
			final ICsResponseListener<CsPvData<T>> callback)
			throws DalException {

		GetValueRequester<T> requester = new GetValueRequester<T>(_jcaContext,
				_typeMapping, _pv, _type, callback) {
			@Override
			protected void onDispose() {
				_openRequests.remove(callback);
			}
		};
		_openRequests.put(callback, requester);
		assert requester != null : "Postcondition: result != null";
		return requester;
	}

	protected final Context getJcaContext() {
		return _jcaContext;
	}

	@Override
	public void dispose() {
		if (_monitor != null) {
			_monitor.dispose();
		}

		for (AbstractChannelOperator o : _openRequests.values()) {
			o.dispose();
		}
	}

}
