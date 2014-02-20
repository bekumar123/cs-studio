package org.csstudio.dal2.simulator.service;

import java.util.concurrent.ScheduledFuture;

import org.csstudio.dal2.service.cs.ICsOperationHandle;

class OperationHandle implements ICsOperationHandle {

	private ScheduledFuture<?> _future;

	public OperationHandle(ScheduledFuture<?> future) {
		_future = future;
	}

	@Override
	public void cancel() {
		_future.cancel(false);
	}

}