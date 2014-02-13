package org.csstudio.dal2.service.cs;

public interface ICsResponseListener<T> {

	public void onFailure(Throwable throwable);

	public void onSuccess(T response);
	
}
