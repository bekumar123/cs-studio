package org.remotercp.service.connection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ConnectionActivator implements BundleActivator {

	private static BundleContext bundleContext;

	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}
}
