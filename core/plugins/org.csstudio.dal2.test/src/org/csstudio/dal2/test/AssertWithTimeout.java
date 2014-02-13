package org.csstudio.dal2.test;


/**
 * <i>Utility class for tests with concurrency</i>
 * <p>
 * Performs the checks implemented in {@link #performCheck()} repeatedly until
 * the check succeeds or a timeout occurs
 * <p>
 * Example:
 * 
 * <pre>
 * new AssertWithTimeout(1000) {
 * 	protected void performCheck() throws Exception {
 * 		verify(listener, times(1))
 * 				.connectionChanged(&quot;TestDal:ConstantPV&quot;, true);
 * 		verify(listener, atLeast(1)).valueChanged(5L);
 * 	}
 * };
 * </pre>
 * 
 */
public abstract class AssertWithTimeout {

	public AssertWithTimeout(long timeout) {
		assert timeout > 0;

		Throwable lastError;

		long startTime = System.currentTimeMillis();
		do {
			try {
				performCheck();
				return;
			} catch (Throwable e) {
				lastError = e;
				try {
					Thread.sleep(25);
				} catch (InterruptedException e1) {
				}
			}
		} while (startTime + timeout > System.currentTimeMillis());

		throw new AssertionError("Check failed for " + timeout + " ms.",
				lastError);
	}

	protected abstract void performCheck() throws Exception;

}