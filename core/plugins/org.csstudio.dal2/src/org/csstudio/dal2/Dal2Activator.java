package org.csstudio.dal2;

import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.dal2.service.impl.DalServiceFactory;
import org.csstudio.servicelocator.ServiceLocator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Dal2Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext bundleContext) throws Exception {
		Dal2Activator.context = bundleContext;
		ServiceLocator.registerService(IDalServiceFactory.class, new DalServiceFactory());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext bundleContext) throws Exception {
		Dal2Activator.context = null;
	}

}
