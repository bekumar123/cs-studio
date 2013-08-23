package org.csstudio.dal.simple.compatibility;

import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.dal2.service.IResponseListener;

/**
 * Adapter to use the old dal {@link ChannelListener} as an {@link IPvListener}
 * with an {@link IPvAccess}
 */
public class ListenerAdapter<T> implements IPvListener<T> {

	private final Logger _logger = Logger.getLogger(ListenerAdapter.class
			.getName());

	private final ChannelListener _channelListener;
	private final PropertyChangeListener _propertyChangeListener;

	private IPvAccess<T> _pvAccess;

	public ListenerAdapter(IPvAccess<T> pvAccess,
			ChannelListener channelListener) {
		_pvAccess = pvAccess;
		_channelListener = channelListener;
		_propertyChangeListener = null;
	}

	public ListenerAdapter(IPvAccess<T> pvAccess,
			PropertyChangeListener propertyChangeListener) {
		_channelListener = null;
		_propertyChangeListener = propertyChangeListener;
	}

	@Override
	public void connectionChanged(IPvAccess<T> pvAccess, boolean isConnected) {
		if (_channelListener != null) {

			/*
			 * In old DAL the values seem to be available directly when
			 * connection state changes. Therefore the Adapter requests values
			 * before firing event.
			 */
			if (_pvAccess.hasLastKnownValue()) {
				AnyDataChannel channel = new AnyDataChannelCompatibilityAdapter<T>(
						_pvAccess);
				_channelListener.channelStateUpdate(channel);
			} else {
				try {
					_pvAccess.getValue(new IResponseListener<T>() {

						@Override
						public void onFailure(Throwable throwable) {
							_logger.log(Level.SEVERE,
									"Error providing connection state update",
									throwable);
						}

						@Override
						public void onSuccess(T response) {
							AnyDataChannel channel = new AnyDataChannelCompatibilityAdapter<T>(
									_pvAccess);
							_channelListener.channelStateUpdate(channel);
						}

						@Override
						public void onTimeout() {
							AnyDataChannel channel = new AnyDataChannelCompatibilityAdapter<T>(
									_pvAccess);
							_logger.log(Level.SEVERE, "Timeout occured");
							_channelListener.channelStateUpdate(channel);
						}
					});
				} catch (DalException e) {
					_logger.log(Level.SEVERE,
							"Error providing connection state update", e);
				}
			}
		}
	}

	@Override
	public void valueChanged(IPvAccess<T> source, T value) {

		if (_channelListener != null) {
			_channelListener
					.channelDataUpdate(new AnyDataChannelCompatibilityAdapter<T>(
							_pvAccess));
		}

		if (_propertyChangeListener != null) {
			// TODO ArS: fire property change event for changed characteristics
			// _propertyChangeListener.propertyChange()
		}
	}

}