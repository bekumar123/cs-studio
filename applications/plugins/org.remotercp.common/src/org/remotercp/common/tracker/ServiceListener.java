package org.remotercp.common.tracker;

/**
 * This interface is to be implemented by all classes that want to be notified
 * when the service they register for gets added or removed from the
 * {@link ServiceProvider} container.
 * <p>
 * For a demo of the usage of this interface and the registration see the
 * {@link ServiceProvider} class API.
 * 
 * @param <S>
 *            - represents the type of the service and should be the
 *            corresponding interface
 */
public interface ServiceListener<S>
{

	/**
	 * Gets called when the service in question gets added and is available.
	 * 
	 * @param service
	 *            - the service implementation in question
	 */
	void bindService(S service);

	/**
	 * Gets called when the service in question gets removed and becomes
	 * unavailable.
	 * 
	 * @param service
	 *            - the service implementation in question
	 */
	void unbindService(S service);

}
