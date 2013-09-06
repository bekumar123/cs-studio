package org.csstudio.dal2.epics.service;

import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Channel.ConnectionState;
import gov.aps.jca.Context;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.ICsOperationHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChannelOperator implements ConnectionListener, ICsOperationHandle {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractChannelOperator.class);
	
	private static final Executor EXECUTOR = Executors
			.newSingleThreadExecutor();

	/**
	 * JCA Context
	 */
	private Context _context;

	private PvAddress _address;

	private Channel _channel;

	private AtomicBoolean _onceConnected = new AtomicBoolean(false);

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
	public synchronized final void connectionChanged(final ConnectionEvent ev) {

		Runnable handler = new Runnable() {
			@Override
			public void run() {
				synchronized (AbstractChannelOperator.this) {
					onConnectionChanged(ev);

					if (_channel.getConnectionState() == ConnectionState.CONNECTED
							&& !_onceConnected.getAndSet(true)) {
						onFirstConnect(ev);
						AbstractChannelOperator.this.notifyAll();
					}
				}
			}
		};

		if (_channel == null) {
			// Event ocured synchronously while running constructor.
			// Perform event handing in separate task to ensure constructors
			// been completed.
			EXECUTOR.execute(handler);
		} else {
			handler.run();
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
	
	public PvAddress getAddress() {
		return _address;
	}

}