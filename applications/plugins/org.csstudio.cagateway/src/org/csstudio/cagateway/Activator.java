
package org.csstudio.cagateway;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.cagateway";

	// The shared instance
	private static Activator plugin;

	private static BundleContext bundleContext;

	public static BundleContext getBundleContext() {
	    return bundleContext;
	}

    public static Activator getDefault() {
        return plugin;
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(final BundleContext context) throws Exception {
	    Activator.plugin = this;
	    Activator.bundleContext = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(final BundleContext context) throws Exception {
	    Activator.plugin = null;
	    Activator.bundleContext = null;
	}
}
