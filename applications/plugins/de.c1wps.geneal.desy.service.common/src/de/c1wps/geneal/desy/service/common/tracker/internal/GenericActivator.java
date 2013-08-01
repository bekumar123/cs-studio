package de.c1wps.geneal.desy.service.common.tracker.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class GenericActivator implements BundleActivator {

	private static BundleContext bundleContext;

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		bundleContext = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

	}

}
