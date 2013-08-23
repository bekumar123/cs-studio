package org.csstudio.dal.simple.compatibility;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.Response;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.Identifiable;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IResponseListener;
import org.csstudio.dal2.service.TimeoutException;

public class AsyncRequestAdapter<T> implements IResponseListener<T> {

	private RequestImpl<T> _request;

	private Identifiable _source = null; // TODO set source

	public AsyncRequestAdapter(IPvAccess<T> pvAccess,
			ResponseListener<T> listener) {
		_request = new RequestImpl<T>(_source, listener);
	}

	public RequestImpl<T> getRequest() {
		return _request;
	}

	@Override
	public void onFailure(Throwable throwable) {

		String idTag = null;
		boolean success = false;
		Exception error;
		if (throwable instanceof Exception) {
			error = (Exception) throwable;
		} else {
			error = new Exception(throwable);
		}

		DynamicValueCondition cond = null; // TODO create
											// DynamicValueCondition
		Timestamp timestamp = null; // TODO set timestamp
		boolean last = true; // TODO set last

		Response<T> response = new ResponseImpl<T>(_source, _request, null,
				idTag, success, error, cond, timestamp, last);
		_request.addResponse(response);

	}
	
	@Override
	public void onTimeout() {
		try {
			throw new TimeoutException("Timeout occured");
		} catch (TimeoutException e) {
			onFailure(e);
		}
	}

	@Override
	public void onSuccess(T value) {

		String idTag = null;
		boolean success = true;
		Exception error = null;
		DynamicValueCondition cond = null; // TODO create
											// DynamicValueCondition
		Timestamp timestamp = null; // TODO set timestamp
		boolean last = true; // TODO set last

		Response<T> response = new ResponseImpl<T>(_source, _request,
				value, idTag, success, error, cond, timestamp, last);

		_request.addResponse(response);
	}
}