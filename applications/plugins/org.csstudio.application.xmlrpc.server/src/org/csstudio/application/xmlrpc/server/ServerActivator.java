
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.application.xmlrpc.server;

import org.csstudio.archive.common.service.ArchiveReaderServiceTracker;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ServerActivator implements BundleActivator {

    public static final String PLUGIN_ID = "org.csstudio.application.xmlrpc.server";

    private static ServerActivator bundle;

	private static BundleContext context;

    private ArchiveReaderServiceTracker _archiveServiceTracker;

    public static ServerActivator getBundle() {
        return bundle;
    }

    public static BundleContext getContext() {
		return context;
	}

	@Override
    public void start(BundleContext bundleContext) throws Exception {
	    ServerActivator.bundle = this;
	    ServerActivator.context = bundleContext;
        _archiveServiceTracker = new ArchiveReaderServiceTracker(bundleContext);
        _archiveServiceTracker.open();

        //MySQLArchiveServiceImpl service = _archiveServiceTracker.getService();
	}

	@Override
    public void stop(BundleContext bundleContext) throws Exception {
	    ServerActivator.bundle = null;
	    ServerActivator.context = null;
	}

    public IArchiveReaderFacade getArchiveEngineService() throws OsgiServiceUnavailableException {
        final IArchiveReaderFacade service =
            (IArchiveReaderFacade) _archiveServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Archive engine config service unavailable.");
        }
        return service;
    }
}
