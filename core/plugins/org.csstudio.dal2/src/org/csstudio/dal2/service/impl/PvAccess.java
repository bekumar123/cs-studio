package org.csstudio.dal2.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.IResponseListener;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PvAccess<T> implements IPvAccess<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PvAccess.class); 
	
	/**
	 * Timeout (s) used for synchronous getValue
	 */
	private static final int SYNC_GET_VALUE_DEFAULT_TIMEOUT = 30;

	private static final ScheduledExecutorService EXECUTOR = Executors
			.newScheduledThreadPool(3);

	/**
	 * The type used to connect the pv
	 */
	private final Type<T> _type;

	/**
	 * The underlying control system specific pv access object
	 */
	private ICsPvAccess<T> _csPvAccess;

	/**
	 * The listener (monitor) used for the underlying control system
	 */
	private CsPvListener _csPvListener = null;

	/**
	 * The currently registered listener (monitors)
	 */
	private List<IPvListener<T>> _listener = Collections
			.synchronizedList(new ArrayList<IPvListener<T>>());

	/**
	 * Last known Characteristics
	 */
	private Characteristics _lastKnownCharacteristics = null;

	/**
	 * The last value received from control system
	 */
	private T _lastKnownValue;

	/**
	 * The state of the connection. (Only monitors will create a durable
	 * connection.)
	 */
	private AtomicBoolean _connected = new AtomicBoolean(false);

	/**
	 * The type of listener used with this pv access
	 */
	private ListenerType _listenerType;

	/**
	 * Constructor
	 * 
	 * @param csPvAccess
	 * @param type
	 * 
	 * @require csPvAccess != null
	 * @require type != null
	 * @require listenerType != null
	 */
	public PvAccess(ICsPvAccess<T> csPvAccess, Type<T> type,
			ListenerType listenerType) {
		assert csPvAccess != null : "Precondition: csPvAccess != null";
		assert type != null : "Precondition: type != null";
		assert listenerType != null : "Precondition: listenerType != null";

		_csPvAccess = csPvAccess;
		_type = type;
		_listenerType = listenerType;
	}

	@Override
	public Type<T> getType() {
		assert _type != null : "Postcondition: result != null";
		return _type;
	}

	@Override
	public PvAddress getPVAddress() {
		PvAddress pvAddress = _csPvAccess.getPvAddress();
		assert pvAddress != null : "Postcondition: result != null";
		return pvAddress;
	}

	@Override
	public ListenerType getListenerType() {
		assert _listenerType != null : "Postcondition: result != null";
		return _listenerType;
	}

	@Override
	public synchronized void registerListener(IPvListener<T> listener)
			throws DalException {

		if (listener == null) {
			throw new IllegalArgumentException("Listener must not be null");
		}
		if (_listener.contains(listener)) {
			throw new IllegalStateException("Listener allready registred");
		}

		if (_csPvListener == null) {
			_csPvListener = new CsPvListener(_listenerType);
			_csPvAccess.initMonitor(_csPvListener);
		}
		_listener.add(listener);
	}

	@Override
	public synchronized void deregisterListener(IPvListener<T> listener)
			throws DalException {

		if (listener == null) {
			throw new IllegalArgumentException("Listener must not be null");
		}
		if (!_listener.remove(listener)) {
			throw new IllegalStateException("Listener is not registred");
		}

		if (_listener.isEmpty()) {
			_csPvAccess.stopMonitor();
			_csPvListener = null;
		}
	}

	@Override
	public synchronized void deregisterAllListener() {
		_listener.clear();
		if (_csPvAccess.hasMonitor()) {
			_csPvAccess.stopMonitor();
		}
		_csPvListener = null;
	}

	@Override
	public void getValue(final IResponseListener<T> callback)
			throws DalException {
		getValue(SYNC_GET_VALUE_DEFAULT_TIMEOUT, TimeUnit.SECONDS, callback);
	}

	@Override
	public void getValue(long timeout, TimeUnit unit,
			final IResponseListener<T> callback) throws DalException {
		new AsyncValueRequester(_csPvAccess, callback, timeout, unit);
	}

	@Override
	public T getValue() throws DalException {
		return getValue(SYNC_GET_VALUE_DEFAULT_TIMEOUT, TimeUnit.SECONDS);
	}

	@Override
	public T getValue(long timeout, TimeUnit unit) throws DalException {
		SynchronizingResponseLister<CsPvData<T>> synchronizingListener = new SynchronizingResponseLister<CsPvData<T>>();

		_csPvAccess.getValue(synchronizingListener);

		CsPvData<T> result = synchronizingListener.getValue(timeout, unit);

		updateLastKnown(result.getValue(), result.getCharacteristics());
		return result.getValue();
	}

	@Override
	public synchronized Characteristics getLastKnownCharacteristics() {
		return _lastKnownCharacteristics;
	}

	@Override
	public synchronized T getLastKnownValue() {
		return _lastKnownValue;
	}

	@Override
	public synchronized boolean hasLastKnownValue() {
		return _lastKnownValue != null;
	}

	@Override
	public synchronized boolean isConnected() {
		return _connected.get();
	}

	private synchronized void updateLastKnown(T value,
			Characteristics characteristics) {

		_lastKnownValue = value;

		if (_lastKnownCharacteristics == null) {
			_lastKnownCharacteristics = characteristics;
		} else {
			_lastKnownCharacteristics = _lastKnownCharacteristics
					.createUpdate(characteristics);
		}
	}

	/**
	 * The AsyncValueRequester performs the async get value request on the
	 * control system. It is delegating the callback from control system (
	 * {@link ICsResponseListener}) to {@link IResponseListener}
	 * <p>
	 * In addition this listener performs a timeout detection
	 */
	private class AsyncValueRequester implements
			ICsResponseListener<CsPvData<T>> {

		private IResponseListener<T> callback;
		private ScheduledFuture<?> timeoutTask;
		private ICsOperationHandle csOperationHandle;

		public AsyncValueRequester(ICsPvAccess<T> _csPvAccess,
				IResponseListener<T> callback, long timeout, TimeUnit unit)
				throws DalException {

			this.callback = callback;
			synchronized (this) {
				this.csOperationHandle = _csPvAccess.getValue(this); 
				this.timeoutTask = EXECUTOR.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							synchronized (AsyncValueRequester.this) {
								csOperationHandle.cancel();
								AsyncValueRequester.this.callback.onTimeout();
							}
						} catch (Exception e) {
							LOG.error("errror handling timeout", e);
						}
					}
				}, timeout, unit);
			}
		}

		@Override
		public synchronized void onSuccess(CsPvData<T> response) {
			if (timeoutTask.cancel(false)) {
				T value = response.getValue();
				updateLastKnown(value, response.getCharacteristics());
				callback.onSuccess(value);
			}
		}

		@Override
		public synchronized void onFailure(Throwable throwable) {
			if (timeoutTask.cancel(false)) {
				callback.onFailure(throwable);
			}
		}
	}

	/**
	 * Private handler registered as PvListener on Control System PV Access.
	 */
	private class CsPvListener implements ICsPvListener<T> {

		private ListenerType _type;

		public CsPvListener(ListenerType type) {
			assert type != null : "Precondition: type != null ";
			_type = type;
		}

		@Override
		public void connectionChanged(String pvName, boolean isConnected) {

			synchronized (PvAccess.this) {
				_connected.set(isConnected);
				for (IPvListener<T> listener : _listener) {
					listener.connectionChanged(PvAccess.this, isConnected);
				}
			}
		}

		@Override
		public void valueChanged(CsPvData<T> data) {
			synchronized (PvAccess.this) {
				updateLastKnown(data.getValue(), data.getCharacteristics());
				for (IPvListener<T> listener : _listener) {
					listener.valueChanged(PvAccess.this, data.getValue());
				}
			}
		}

		@Override
		public void errorReceived(String message) {

			// TODO How to handle message?
			System.err.println(message);
		}

		@Override
		public ListenerType getType() {
			return _type;
		}

	}
}
