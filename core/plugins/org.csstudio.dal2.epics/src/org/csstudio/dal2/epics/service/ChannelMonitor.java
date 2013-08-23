package org.csstudio.dal2.epics.service;

import gov.aps.jca.CAException;
import gov.aps.jca.CASeverity;
import gov.aps.jca.CAStatus;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvListener;

/**
 * Represents a monitor created for a channel
 * 
 * @param <T>
 */
public class ChannelMonitor<T> extends AbstractChannelOperator implements
		MonitorListener {

	private final Logger _logger = Logger.getLogger(ChannelMonitor.class
			.getName());

	private ICsPvListener<T> _listener;
	private Monitor _subscription;
	private Type<T> _type;

	public ChannelMonitor(Context jcaContext, PvAddress pv, Type<T> type,
			ICsPvListener<T> listener) throws DalException {
		super(jcaContext, pv);
		_listener = listener;
		_type = type;
	}

	@Override
	protected void onConnectionChanged(ConnectionEvent ev) {
		String name = getChannel().getName();

		_logger.log(Level.FINE, "Connection changed (" + getChannel().getName()
				+ "): " + (ev.isConnected() ? "connected" : "not connected"));

		_listener.connectionChanged(name, ev.isConnected());
	}

	@Override
	protected void onFirstConnect(ConnectionEvent ev) {
		try {
			DBRType ctrlType = TypeMapper.getMapper(_type).getDBRCtrlType();
			int mask = getMask(_listener.getType());

			_subscription = getChannel().addMonitor(ctrlType, 0, mask,
					this);
			getContext().flushIO();
		} catch (IllegalStateException e) {
			_logger.log(Level.SEVERE, "Error creating channel monitor for "
					+ getChannel().getName(), e);
			dispose();
		} catch (CAException e) {
			_logger.log(Level.SEVERE, "Error creating channel monitor for "
					+ getChannel().getName(), e);
			dispose();
		}
	}

	@Override
	public void monitorChanged(MonitorEvent ev) {

		CAStatus status = ev.getStatus();

		if (status.isSuccessful()) {
			T value = TypeMapper.getMapper(_type).mapValue(ev.getDBR());
			Characteristics characteristics = new CharacteristicsService()
					.newCharacteristics(ev.getDBR(), getChannel().getHostName());

			_logger.log(Level.FINE, "Monitor changed ("
					+ getChannel().getName() + "): " + value);

			_listener.valueChanged(new CsPvData<T>(value, characteristics));

		} else {

			CASeverity severity = status.getSeverity();
			Level logLevel = Level.SEVERE;
			if (severity.equals(CASeverity.FATAL)
					|| severity.equals(CASeverity.ERROR)
					|| severity.equals(CASeverity.SEVERE)) {
				logLevel = Level.SEVERE;
			} else if (severity.equals(CASeverity.WARNING)) {
				logLevel = Level.WARNING;
			} else if (severity.equals(CASeverity.INFO)) {
				logLevel = Level.INFO;
			}

			_logger.log(logLevel, "Monitor was not successful for channel ("
					+ getChannel().getName() + "): " + status.getMessage());
		}
	}

	@Override
	public void dispose() {

		// the JNI implementation does not fire a connectionChanged-Event,
		// when a listener is deregistered
		// but the CAJ implementation does so. So here we may adapt to this
		// behavior.
		// _listener.connectionChanged(false); // add this for JNI

		try {
			if (_subscription != null) {
				_subscription.clear();
			}
		} catch (CAException e) {
			_logger.log(Level.SEVERE, "Error clearing jca monitor", e);
		}

		super.dispose();
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
