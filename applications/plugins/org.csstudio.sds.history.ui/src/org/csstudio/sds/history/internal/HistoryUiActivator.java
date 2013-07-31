package org.csstudio.sds.history.internal;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.history.domain.listener.ITimeChangeListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.c1wps.geneal.desy.service.common.tracker.GenericServiceTracker;
import de.c1wps.geneal.desy.service.common.tracker.IGenericServiceListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class HistoryUiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.sds.history"; //$NON-NLS-1$

	// The shared instance
	private static HistoryUiActivator plugin;
	
	private GenericServiceTracker<ITimeChangeListener> _timeChangeListenerTracker;
	
	private List<ITimeChangeListener> _timeChangeListeners;
	
	/**
	 * The constructor
	 */
	public HistoryUiActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		_timeChangeListeners = new ArrayList<ITimeChangeListener>();
		_timeChangeListenerTracker = new GenericServiceTracker<>(context, ITimeChangeListener.class);
		_timeChangeListenerTracker.open();
		_timeChangeListenerTracker.addServiceListener(createITimeChangeServiceTracker());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
		_timeChangeListenerTracker.close();
		_timeChangeListenerTracker = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static HistoryUiActivator getDefault() {
		return plugin;
	}
	
	public void addTimeChangeServiceListener(IGenericServiceListener<ITimeChangeListener> timeChangeServiceListener) {
		_timeChangeListenerTracker.addServiceListener(timeChangeServiceListener);
	}
	
	public List<ITimeChangeListener> getTimeChangeListeners() {
		return _timeChangeListeners;
	}
	
	private IGenericServiceListener<ITimeChangeListener> createITimeChangeServiceTracker() {
		return new IGenericServiceListener<ITimeChangeListener>() {
			@Override
			public void bindService(ITimeChangeListener service) {
				_timeChangeListeners.add(service);
				
			}
			@Override
			public void unbindService(ITimeChangeListener service) {
				_timeChangeListeners.remove(service);
			}
		};
	}
}
