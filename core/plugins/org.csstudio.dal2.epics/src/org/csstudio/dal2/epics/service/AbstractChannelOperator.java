package org.csstudio.dal2.epics.service;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Context;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChannelOperator implements ConnectionListener,
		ICsOperationHandle {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractChannelOperator.class);

	/**
	 * Single Thread executor. This is used to handle CJA-Callbacks in a
	 * different thread.
	 */
	// protected static final Executor EXECUTOR = Executors
	// .newSingleThreadExecutor();

	/**
	 * JCA Context
	 */
	private Context _context;

	private PvAddress _address;

	private Channel _channel;

	private AtomicBoolean _onceConnected = new AtomicBoolean(false);

	private Type<?> _nativeType;

	public AbstractChannelOperator(Context context, PvAddress address)
			throws DalException {
		_context = context;
		_address = address;

		try {
			synchronized (this) {
				_channel = _context.createChannel(_address.getAddress(), this);
				assert _channel != null : "Check: _channel != null";
			}
		} catch (CAException e) {
			throw new DalException("Error connection channel: " + _address, e);
		} catch (IllegalStateException e) {
			throw new DalException("Error connection channel: " + _address, e);
		}
	}

	@Override
	public final void connectionChanged(final ConnectionEvent ev) {
		try {
			synchronized (AbstractChannelOperator.this) {

				boolean connected = _channel.getConnectionState() == ConnectionState.CONNECTED;

				if (connected) {
					DBRType dbrType = getChannel().getFieldType();
					_nativeType = TypeMapper.getType(dbrType);
				}

				onConnectionChanged(ev);

				if (connected) {
					if (_onceConnected.getAndSet(true)) {
						onReconnect(ev);
					} else {
						onFirstConnect(ev);
					}
				}
			}
		} catch (Throwable t) {
			LOGGER.error("Error handling connection changed event for pv {}",
					_address.getAddress(), t);
		}
	}

	/**
	 * Override this method to react on connection state changes
	 */
	protected void onConnectionChanged(ConnectionEvent ev) {
		// override to implement
	}

	/**
	 * Override this method to react on (first) existing connection
	 */
	protected void onFirstConnect(ConnectionEvent ev) {
		// override to implement
	}

	/**
	 * Override this method to react on reconnect
	 */
	protected void onReconnect(ConnectionEvent ev) {
		// override to implement
	}

	/**
	 * destroy t
	 */
	public void dispose() {

		onDispose();

		try {
			_channel.removeConnectionListener(this);
		} catch (Exception e) {
			LOGGER.warn("Error removing connections listener from channel.", e);
		}

		try {
			_channel.destroy();
		} catch (Exception e) {
			LOGGER.warn("Error destroying channel.", e);
		}
	}

	@Override
	public void cancel() {
		dispose();
	}

	/**
	 * This method is called when the channel operator is to be disposed
	 * <p>
	 * <i>template method</i>
	 */
	protected void onDispose() {
		// Override to implement
	}

	protected final Context getContext() {
		return _context;
	}

	protected final Channel getChannel() {
		return _channel;
	}

	protected final Type<?> getNativeType() {
		return _nativeType;
	}

	public final PvAddress getAddress() {
		return _address;
	}

}