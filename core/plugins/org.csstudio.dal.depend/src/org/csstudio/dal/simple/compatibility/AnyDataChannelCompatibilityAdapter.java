package org.csstudio.dal.simple.compatibility;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.simple.AnyData;
import org.csstudio.dal.simple.AnyDataChannel;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal2.service.IPvAccess;

public class AnyDataChannelCompatibilityAdapter<T> implements AnyDataChannel {

	private IPvAccess<T> _pvAccess;

	public AnyDataChannelCompatibilityAdapter(IPvAccess<T> pvAccess) {
		_pvAccess = pvAccess;
	}
	
	@Override
	public DynamicValueProperty<T> getProperty() {
		return new DynamicValuePropertyCompatibilityAdapter<T>(_pvAccess);
	}

	@Override
	public AnyData getData() {
		return AnyDataCompatibilityAdapterFactory.createAnyData(_pvAccess);
	}
	
	/*
	 * The following methods are not supported by this implementation
	 */

	@Override
	@Deprecated
	public String getUniqueName() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void addListener(ChannelListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void removeListener(ChannelListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public ChannelListener[] getListeners() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void start() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void startSync() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isRunning() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isConnected() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isWriteAllowed() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public String getStateInfo() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void stop() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void setValueAsObject(Object new_value) throws RemoteException {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public boolean isMetaDataInitialized() {
		throw new UnsupportedOperationException();
	}


}
