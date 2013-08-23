package org.csstudio.dal2.epics.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsResponseListener;

class ResponseCallbackHandlerMock<T> implements ICsResponseListener<CsPvData<T>> {
	
	private CsPvData<T> _response;
	private Throwable _throwable;
	private AtomicBoolean _finished = new AtomicBoolean(false);
	
	@Override
	public void onSuccess(CsPvData<T> response) {
		_response = response;
		_finished.set(true);
	}
	
	@Override
	public void onFailure(Throwable throwable) {
		this._throwable = throwable;
		this._finished.set(true);
	}
	
	public boolean isFinished() {
		return _finished.get();
	}
	
	public CsPvData<T> getValue() {
		if (_throwable != null) {
			throw new RuntimeException("Received Failure", _throwable);
		}
		
		return _response;
	}
	
}