package org.remotercp.common.tracker;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class acts as a bridge between the OSGI Service Tracker mechanism and
 * the {@link ServiceProvider} container.
 * <p>
 * When a service offering plug-in registers a service, nothing will happen at
 * first. As soon as an interested plug-in tries to obtain a service or
 * registers as a listener for this service, this ServiceBridge will be created
 * and wait for notifications which are then propagated to the
 * {@link ServiceProvider}.
 * <p>
 * For a demo of the usage of this class see the {@link ServiceProvider} class
 * API.
 * 
 * @see ServiceProvider
 * 
 * @param <S>
 *            - represents the type of the service and should be the
 *            corresponding interface
 */
public final class ServiceBridge<S> extends ServiceTracker {

	/**
	 * The class object of the service's interface. Used to identify the
	 * service.
	 */
	private Class<S> clazz;

	/**
	 * Instantiates the bridge.
	 * 
	 * @param context
	 *            - the {@link BundleContext}
	 * @param clazz
	 *            - the class object of the service's interface
	 */
	ServiceBridge(BundleContext context, Class<S> clazz) {
		// the ServiceTracker works with only the name
		// of the interfaces class. For the sake of generics we use
		// the class object itself.
		super(context, clazz.getName(), null);

		this.clazz = clazz;
	}

	/**
	 * Called when the service this bridge is registered for is added.
	 * <p>
	 * Makes sure that the added service ends up in the {@link ServiceProvider}
	 * container.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object addingService(ServiceReference reference) {
		S service = (S) super.addingService(reference);

		ServiceProvider.addService(clazz, service);

		return service;
	}

	/**
	 * Called when the service this bridge is registered for gets removed.
	 * <p>
	 * Makes sure that the added service ends up in the {@link ServiceProvider}
	 * container.
	 */
	@Override
	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);

		ServiceProvider.removeService(clazz);
	}

}
