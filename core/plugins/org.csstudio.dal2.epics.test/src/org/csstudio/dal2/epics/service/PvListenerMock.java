package org.csstudio.dal2.epics.service;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvListener;

public class PvListenerMock<T> implements ICsPvListener<T> {

	private int _connectionChangedCalled = 0;
	private CsPvData<T> _value = null;
	private boolean _isConnected;
	private Characteristics _characteristics;
	private ListenerType _listenerType;

	public PvListenerMock(ListenerType type) {
		_listenerType = type;
	}

	@Override
	public void connectionChanged(String pvName, final boolean isConnected) {
		_isConnected = isConnected;
		_connectionChangedCalled++;
	}

	public int getConnectionChangedCalled() {
		return _connectionChangedCalled;
	}

	public CsPvData<T> getValue() {
		return _value;
	}

	public boolean isConnected() {
		return _isConnected;
	}

	public Characteristics getCharacteristics() {
		return _characteristics;
	}

	@Override
	public void valueChanged(CsPvData<T> value) {
		_value = value;
	}

	@Override
	public void errorReceived(String message) {
		throw new RuntimeException(message);
	}

	@Override
	public ListenerType getType() {
		return _listenerType;
	}
}