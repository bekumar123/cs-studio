/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.engine.service;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.engine.ArchiveEngineActivator;
import org.csstudio.archive.common.engine.ArchiveEnginePreferencesService;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IDalServiceFactory;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.servicelocator.ServiceLocator;

/**
 * Implementation of the service provider encapsulating all OSGi services for this application.
 *
 * Can be replaced (mocked out) in a test environment, hence decoupling the real service retrieval
 * via either service trackers, static references, declarative services from the service usage.
 *
 * @author bknerr
 * @since Mar 23, 2011
 */
public class ServiceProvider implements IServiceProvider {
    private IDalService _dalService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IArchiveEngineFacade getEngineFacade() throws OsgiServiceUnavailableException {
        return ArchiveEngineActivator.getDefault().getArchiveEngineService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public ArchiveEnginePreferencesService getPreferencesService() {
        return ArchiveEngineActivator.getDefault().getPreferencesService();
    }

    @Override
    @Nonnull
    public IDalService getDalService() throws OsgiServiceUnavailableException {

        if (_dalService == null) {
            final IDalServiceFactory dalServiceFactory = ServiceLocator.getService(IDalServiceFactory.class);
            if (dalServiceFactory == null) {
                throw new OsgiServiceUnavailableException("Missing service: " + IDalServiceFactory.class.getName());
            }
            _dalService = dalServiceFactory.newDalService();
        }
        return _dalService;
    }
}
