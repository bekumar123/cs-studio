package org.remotercp.common;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CommonActivator implements BundleActivator {
	
	private static BundleContext bundleContext;

	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	public void stop(BundleContext context) throws Exception {
		bundleContext = null;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

}

