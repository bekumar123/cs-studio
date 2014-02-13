package org.csstudio.dal2.service;


public interface IResponseListener<T> {

	public void onFailure(Throwable throwable);

	public void onSuccess(T response);

	public void onTimeout();
		
}
