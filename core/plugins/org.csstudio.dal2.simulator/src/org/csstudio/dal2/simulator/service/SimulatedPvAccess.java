package org.csstudio.dal2.simulator.service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.Characteristics.Builder;
import org.csstudio.dal2.dv.ConnectionState;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;

public class SimulatedPvAccess<T> implements ICsPvAccess<T> {

	private static final long DELAY = 50;
	private ValueProvider<T> _valueProvider;
	private ScheduledExecutorService _executor;
	private Type<T> _type;
	private T _value;
	private ICsPvListener<T> _listener;
	private PvAddress _address;
	private boolean _connected;
	private ConnectionState _connectionState = ConnectionState.NOT_REQUESTED;
	private ScheduledFuture<?> _updateFuture;

	public SimulatedPvAccess(PvAddress address, Type<T> type,
			ValueProvider<T> valueProvider, ScheduledExecutorService executor,
			long period) {
		_address = address;
		_type = type;
		_valueProvider = valueProvider;
		_executor = executor;
		_value = _valueProvider.get();

		_updateFuture = _executor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				updateValue();
			}
		}, period, period, TimeUnit.MILLISECONDS);
	}

	@Override
	public ICsOperationHandle getValue(
			final ICsResponseListener<CsPvData<T>> callback)
			throws DalException {

		ScheduledFuture<?> future = _executor.schedule(new Runnable() {
			@Override
			public void run() {
				callback.onSuccess(createCsPvData());
			}
		}, DELAY, TimeUnit.MILLISECONDS);

		return new OperationHandle(future);
	}

	@Override
	public boolean hasMonitor() {
		return _listener != null;
	}

	@Override
	public synchronized void initMonitor(ICsPvListener<T> listener)
			throws DalException {
		_listener = listener;
		_connectionState = ConnectionState.NEVER_CONNECTED;
		_executor.schedule(new Runnable() {
			@Override
			public void run() {
				setConnected(true);
			}
		}, DELAY, TimeUnit.MILLISECONDS);
	}

	@Override
	public synchronized void stopMonitor() {
		setConnected(false);
		_listener = null;
	}

	@Override
	public PvAddress getPvAddress() {
		return _address;
	}

	@Override
	public synchronized ConnectionState getConnectionState() {
		return _connectionState;
	}

	private synchronized void setConnected(boolean connected) {
		_connected = connected;
		if (connected) {
			_connectionState = ConnectionState.CONNECTED;
			if (_listener != null) {
				_listener.connected(_address.getAddress(), _type);
			}
		} else {
			_connectionState = ConnectionState.DISCONNECTED;
			if (_listener != null) {
				_listener.disconnected(_address.getAddress());
			}
		}
	}

	private synchronized void updateValue() {
		_value = _valueProvider.get();
		if (_listener != null && _connected) {
			_listener.valueChanged(createCsPvData());
		}
	}

	/**
	 * @return
	 */
	private synchronized CsPvData<T> createCsPvData() {
		Builder builder = Characteristics.builder();
		builder.set(Characteristic.TIMESTAMP, new Timestamp());
		builder.set(Characteristic.STATUS, EpicsAlarmStatus.NO_ALARM);
		builder.set(Characteristic.SEVERITY, EpicsAlarmSeverity.NO_ALARM);
		Characteristics characteristics = builder.build();
		CsPvData<T> csPvData = new CsPvData<T>(_value, characteristics,	_type);
		return csPvData;
	}

	@Override
	public void dispose() {
		
		stopMonitor();
		
		_updateFuture.cancel(true);
	}

}
