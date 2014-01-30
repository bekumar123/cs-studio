package org.csstudio.dal2.epics.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvListener;

/**
 * Mock implementation of an {@link ICsPvListener} to ensure the received events
 * contain an expected list of value
 * 
 * @param <T>
 */
class PvListenerMock2<T> implements ICsPvListener<T> {

	private ListenerType _type;

	private boolean _isConnected;
	private final List<T> _expectedValues;
	private AtomicInteger _valueChgCount = new AtomicInteger(0);
	private AtomicBoolean _hasError = new AtomicBoolean(false);
	private AtomicBoolean _hasStarted = new AtomicBoolean(false);

	// caller provides the list of values which are expected to show up in the
	// given order
	// comparison begins after the first value in the list has been detected
	public PvListenerMock2(ListenerType type, final List<T> expectedValues) {
		_type = type;
		_expectedValues = expectedValues;
	}

	@Override
	public ListenerType getType() {
		return _type;
	}

	public PvListenerMock2(ListenerType type, T... expectedValues) {
		_type = type;
		_expectedValues = Arrays.asList(expectedValues);
	}

	@Override
	public void connected(String pvName, Type<?> nativeType) {
		_isConnected = true;
	}
	
	@Override
	public void disconnected(String pvName) {
		_isConnected = false;
	}

	@Override
	public void valueChanged(final CsPvData<T> data) {
		System.out.println("valueChanged: " + data.getValue());

		if (isFinished()) {
			return;
		}

		if (_hasStarted.get()) {
			_hasError.compareAndSet(false, !isNextExpected(data.getValue()));
		} else {
			_hasStarted.compareAndSet(false, isFirstExpected(data.getValue()));
		}
		
	}

	protected boolean isFirstExpected(final T value) {
		return _expectedValues.get(0).equals(value);
	}

	private boolean isNextExpected(final T value) {

		int currentIndex = _valueChgCount.incrementAndGet();

		T expectedValue = (T) _expectedValues.get(currentIndex);
		boolean result = expectedValue == value;

		if (!result) {
			System.err.println("in " + value + ", exp " + expectedValue
					+ " => " + result);
		}

		return result;
	}

	public boolean isFinished() {
		return (_valueChgCount.get() + 1 >= _expectedValues.size())
				|| _hasError.get();
	}

	public boolean isConnected() {
		return _isConnected;
	}

	public boolean hasError() {
		return _hasError.get();
	}

	@Override
	public void errorReceived(String message) {
		throw new RuntimeException(message);
	}
}