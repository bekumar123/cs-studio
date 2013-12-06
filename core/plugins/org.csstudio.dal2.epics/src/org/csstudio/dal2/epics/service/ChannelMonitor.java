package org.csstudio.dal2.epics.service;

import java.util.concurrent.atomic.AtomicBoolean;

import gov.aps.jca.CAException;
import gov.aps.jca.CASeverity;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a monitor created for a channel
 * 
 * @param <T>
 */
public class ChannelMonitor<T> extends AbstractChannelOperator implements
		MonitorListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChannelMonitor.class);

	private ICsPvListener<T> _listener;
	private Monitor _subscription;
	private Type<T> _type;

	private AtomicBoolean _connected = new AtomicBoolean();

	public ChannelMonitor(Context jcaContext, PvAddress pv, Type<T> type,
			ICsPvListener<T> listener) throws DalException {
		super(jcaContext, pv);
		_listener = listener;
		_type = type;
	}

	@Override
	protected void onConnectionChanged(ConnectionEvent ev) {
		String name = getChannel().getName();
		assert name.equals(getAddress().getAddress());

		boolean connected = ev.isConnected();

		if (connected) {
			_connected.set(true);
			LOGGER.debug("Connection changed ({}): connected", name);
			_listener.connected(name, getNativeType());
		} else if (_connected.getAndSet(connected)) {
			LOGGER.debug("Connection changed ({}): disconnected", name);
			_listener.disconnected(name);

			if (_type == Type.NATIVE) {
				// Dispose monitor when using Type.NATIVE because it will be
				// recreated
				// on reconnect using the potentially changed native type
				disposeMonitor();
			}
		}
	}

	@Override
	protected void onFirstConnect(ConnectionEvent ev) {
		String name = getAddress().getAddress();
		LOGGER.debug("First connect({}): adding monitor {}", name, _listener);
		createMonitor(name);
	}

	@Override
	protected void onReconnect(ConnectionEvent ev) {
		// Recreate Monitor when using Type.NATIVE to ensure the monitor is
		// using the
		// potentially changed native type
		if (_type == Type.NATIVE) {
			String name = getAddress().getAddress();
			LOGGER.debug("Reconnect connect({}): adding monitor {}", name,
					_listener);
			createMonitor(name);
		}
	}

	/**
	 * @param name
	 */
	private void createMonitor(String name) {
		try {
			DBRType ctrlType = TypeMapper.getMapper(_type, getNativeType())
					.getDBRCtrlType();
			ListenerType type = _listener.getType();
			int mask = getMask(type);
			_subscription = getChannel().addMonitor(ctrlType, 0, mask, this);
			getContext().flushIO();
		} catch (IllegalStateException e) {
			LOGGER.error("Error creating channel monitor for {}", name, e);
			dispose();
		} catch (CAException e) {
			LOGGER.error("Error creating channel monitor for {}", name, e);
			dispose();
		}
	}

	@Override
	public void monitorChanged(final MonitorEvent ev) {
		CAStatus status = ev.getStatus();

		if (status.isSuccessful()) {
			try {

				TypeMapper<T> mapper = TypeMapper.getMapper(_type,
						getNativeType());
				final T value = mapper.mapValue(ev.getDBR());

				String hostName = null;

				if (getChannel().getConnectionState() == ConnectionState.CONNECTED) {
					hostName = getChannel().getHostName();
				}

				final Characteristics characteristics = new CharacteristicsService()
						.newCharacteristics(ev.getDBR(), hostName);

				LOGGER.debug("Monitor changed ({}): {}", getAddress()
						.getAddress(), value);

				_listener.valueChanged(new CsPvData<T>(value, characteristics,
						getNativeType()));

			} catch (Throwable t) {
				LOGGER.error("Error handling monitor changed event for pv {}",
						getAddress().getAddress(), t);
			}
		} else {

			CASeverity severity = status.getSeverity();
			if (severity.equals(CASeverity.FATAL)
					|| severity.equals(CASeverity.ERROR)
					|| severity.equals(CASeverity.SEVERE)) {
				LOGGER.error("Monitor was not successful for channel ({}):",
						getChannel().getName(), status.getMessage());
			} else if (severity.equals(CASeverity.WARNING)) {
				LOGGER.warn("Monitor was not successful for channel ({}):",
						getChannel().getName(), status.getMessage());
			} else if (severity.equals(CASeverity.INFO)) {
				LOGGER.info("Monitor was not successful for channel ({}):",
						getChannel().getName(), status.getMessage());
			}
		}
	}

	@Override
	public void dispose() {

		// the JNI implementation does not fire a connectionChanged-Event,
		// when a listener is deregistered
		// but the CAJ implementation does so. So here we may adapt to this
		// behavior.
		if (_connected.getAndSet(false)) {
			String pv = getAddress().getAddress();
			LOGGER.debug("Connection changed ({}): disconnected", pv);
			_listener.disconnected(pv);
		}

		disposeMonitor();

		super.dispose();
	}

	private void disposeMonitor() {
		try {
			if (_subscription != null) {
				_subscription.clear();
				_subscription = null;
			}
		} catch (CAException e) {
			LOGGER.error("error clearing jcs monitor", e);
		}
	}

	public ICsPvListener<T> getListener() {
		return _listener;
	}

	/**
	 * Provides the epics monitor value for the given listener type
	 * 
	 * @param type
	 * @return
	 * @require type != null
	 */
	protected int getMask(ListenerType type) {

		assert type != null : "Precondition: type != null";

		switch (type) {
		case ALARM:
			return Monitor.ALARM;
		case LOG:
			return Monitor.LOG;
		case PROPERTY:
			return Monitor.PROPERTY;
		case VALUE:
		default:
			return Monitor.VALUE;
		}
	}

}
