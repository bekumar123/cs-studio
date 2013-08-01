/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.common.trendplotter;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.logging.Logger;

import org.csstudio.archive.common.service.ArchiveEngineServiceTracker;
import org.csstudio.archive.common.service.ArchiveReaderServiceTracker;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.sds.history.domain.listener.ITimeChangeListener;
import org.csstudio.sds.history.domain.listener.ITimeperiodUpdateListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.c1wps.geneal.desy.service.common.tracker.GenericServiceTracker;
import de.c1wps.geneal.desy.service.common.tracker.IGenericServiceListener;

/** Eclipse Plugin Activator
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Activator extends AbstractUIPlugin
{
    /** Plug-in ID defined in MANIFEST.MF */
	final public static String PLUGIN_ID = "org.csstudio.common.trendplotter";

	/** Checkbox images */
    final public static String ICON_UNCHECKED = "icons/unchecked.gif",
	                           ICON_CHECKED = "icons/checked.gif";

    /** Singleton instance */
    private static Activator plugin;

    /** Logger for this plugin */
    private static Logger logger = Logger.getLogger(PLUGIN_ID);
    
    // FIXME (bknerr) : find out about proper dependency injection for osgi eclipse rcp
    private ArchiveEngineServiceTracker _archiveEngineConfigServiceTracker;
    private ArchiveReaderServiceTracker _archiveReaderServiceTracker;
    
    private GenericServiceTracker<ITimeperiodUpdateListener> _timeperiodUpdateListenerTracker;
    private GenericServiceTracker<ITimeChangeListener> _timeChangeListenerTracker;
    
    private List<ITimeChangeListener> _timeChangeListeners;
    
    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
        
        _archiveEngineConfigServiceTracker = new ArchiveEngineServiceTracker(context);
        _archiveEngineConfigServiceTracker.open();

        _archiveReaderServiceTracker = new ArchiveReaderServiceTracker(context);
        _archiveReaderServiceTracker.open();
        
        _timeperiodUpdateListenerTracker = new GenericServiceTracker<>(context, ITimeperiodUpdateListener.class);
        _timeperiodUpdateListenerTracker.open();
        
        _timeChangeListeners = new ArrayList<ITimeChangeListener>();
        _timeChangeListenerTracker = new GenericServiceTracker<>(context, ITimeChangeListener.class);
        _timeChangeListenerTracker.open();
        _timeChangeListenerTracker.addServiceListener(createITimeChangeServiceTracker());
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        
        if (_archiveEngineConfigServiceTracker != null) {
            _archiveEngineConfigServiceTracker.close();
        }

        if (_archiveReaderServiceTracker != null) {
            _archiveReaderServiceTracker.close();
        }
        
        if (_timeperiodUpdateListenerTracker != null) {
            _timeperiodUpdateListenerTracker.close();
        }
        
        if (_timeChangeListenerTracker != null) {
            _timeChangeListenerTracker.close();
        }
        
        super.stop(context);
    }

    /** @return the shared instance */
    public static Activator getDefault()
    {
        return plugin;
    }

    /** Obtain image descriptor from file within plugin.
     *  @param path Path within plugin to image file
     *  @return {@link ImageDescriptor}
     */
    public ImageDescriptor getImageDescriptor(final String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /** Obtain image from file within plugin.
     *  Uses registry to avoid duplicates and for disposal
     *  @param path Path within plugin to image file
     *  @return {@link Image}
     */
    public Image getImage(final String path)
    {
        Image image = getImageRegistry().get(path);
        if (image == null)
        {
            image = getImageDescriptor(path).createImage();
            getImageRegistry().put(path, image);
        }
        return image;
    }

    /** @return Version code */
    @SuppressWarnings({ "unchecked" })
    public String getVersion()
    {
        final Dictionary<String, String> headers = getBundle().getHeaders();
        return headers.get("Bundle-Version");
    }

    /** @return Logger for this plugin */
    public static Logger getLogger()
    {
        return logger;
    }
    
    /**
     * Returns the archive reader service from the service tracker.
     * @return the archive service or <code>null</code> if not available.
     * @throws OsgiServiceUnavailableException
     */
    public IArchiveReaderFacade getArchiveReaderService() throws OsgiServiceUnavailableException {
        final IArchiveReaderFacade service =
            (IArchiveReaderFacade) _archiveReaderServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive reader service unavailable.");
        }
        return service;
    }
    
    
    public void addUpdateTimeperiodServiceListener(IGenericServiceListener<ITimeperiodUpdateListener> serviceListener) {
        _timeperiodUpdateListenerTracker.addServiceListener(serviceListener);
    }

    public void addTimeChangeServiceListener(IGenericServiceListener<ITimeChangeListener> serviceListener) {
        _timeChangeListenerTracker.addServiceListener(serviceListener);
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
