package org.csstudio.utility.quickstart;

import org.csstudio.sds.ui.autostart.IRunModeBoxAutostartService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.quickstart";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<IRunModeBoxAutostartService, IRunModeBoxAutostartService> runModeBoxAutostartServiceTracker;

	private static final Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//Creating runModeBoxAutostartServiceTracker here make sometimes problems because the workbench is not yet available
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		runModeBoxAutostartServiceTracker.close();

		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public IRunModeBoxAutostartService getRunModeBoxAutostartService() {
		if (runModeBoxAutostartServiceTracker == null) {
			LOG.error("Wait for Workbench before creating auto start service");
			try {
				waitForWorkbench();
			} catch (InterruptedException e1) {
				LOG.error("Workbench is not available!");
			}
			LOG.error("Eclipse Workbench is available");
			runModeBoxAutostartServiceTracker = new ServiceTracker<IRunModeBoxAutostartService, IRunModeBoxAutostartService>(plugin.getBundle().getBundleContext(), IRunModeBoxAutostartService.class, null);
			runModeBoxAutostartServiceTracker.open();
		}
		IRunModeBoxAutostartService result = null;
		
		try {
			result = runModeBoxAutostartServiceTracker.waitForService(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	/**
	 * Wait until the workbench is available to start SDS displays.
	 * 
	 * @throws InterruptedException
	 */
	private void waitForWorkbench() throws InterruptedException {
		boolean workbenchNotAvailable = true;
		while (workbenchNotAvailable) {
			try {
				final IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench != null) {
					workbenchNotAvailable = false;
				}
			} catch (final IllegalStateException e) {
				// TODO (jhatje) : what shall happen here?
			}
			Thread.sleep(1000);
		}
	}

}
