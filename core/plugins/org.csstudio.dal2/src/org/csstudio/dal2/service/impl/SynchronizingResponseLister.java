package org.csstudio.dal2.service.impl;

import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.TimeoutException;
import org.csstudio.dal2.service.cs.ICsResponseListener;

class SynchronizingResponseLister<T> extends Synchronizer implements ICsResponseListener<T> {
	
	private Throwable _throwable;

	private T _value;

	@Override
	public synchronized void onFailure(Throwable throwable) {
		_throwable = throwable;
		setDone();
	}

	@Override
	public void onSuccess(T value) {
		_value = value;
		setDone();
	}
	
	public T getValue(long timeout, TimeUnit unit) throws DalException, TimeoutException {
		
		waitForDone(timeout, unit, "Timeout waiting for response.");
		
		if (_throwable != null) {
			throw new DalException(_throwable);
		}
		
		return _value;
	}
	
}