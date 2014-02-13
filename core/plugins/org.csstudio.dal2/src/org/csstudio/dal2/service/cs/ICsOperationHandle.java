package org.csstudio.dal2.service.cs;


/**
 * Handle to an async operation on a channel
 */
public interface ICsOperationHandle {

	/**
	 * Cancel the operation. Does nothing if the operation is completed
	 */
	void cancel();
	
}
