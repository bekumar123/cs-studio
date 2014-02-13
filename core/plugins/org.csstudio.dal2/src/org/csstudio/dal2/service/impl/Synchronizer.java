package org.csstudio.dal2.service.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.csstudio.dal2.service.TimeoutException;

/**
 * The synchronizer helps transforming asynchronous into synchronous calls.
 */
public class Synchronizer {

	private CountDownLatch latch = new CountDownLatch(1);

	/**
	 * Waits until {@link #setDone()} is called.
	 * 
	 * @param timeout
	 *            Timeout in ms
	 * @param timeoutMessage
	 *            message used for a {@link TimeoutException}.
	 * 
	 * @throws TimeoutException
	 */
	protected void waitForDone(long timeout, TimeUnit unit,
			String timeoutMessage) throws TimeoutException {

		Boolean timeoutOccured = null;

		while (timeoutOccured == null) {
			try {
				timeoutOccured = !latch.await(timeout, unit);
			} catch (InterruptedException e) {
			}
		}

		if (timeoutOccured) {
			throw new TimeoutException(timeoutMessage);
		}
	}

	/**
	 * notifies waiting threads about the job to be done 
	 */
	public void setDone() {
		latch.countDown();
	}

}
