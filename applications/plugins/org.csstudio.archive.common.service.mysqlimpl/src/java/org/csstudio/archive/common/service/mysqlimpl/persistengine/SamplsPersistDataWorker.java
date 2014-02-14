/*
 * Copyright (c) 2013 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.util.List;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.epics.pvmanager.TypeSupport;

import com.google.common.collect.Lists;

/**
 * TODO (xuwenhu) :
 *
 * @author xuwenhu
 * @since 18.07.2013
 */
public class SamplsPersistDataWorker extends PersistDataWorker {

    private final String _name;
    private final List<Object> _rescueDataList = Lists.newLinkedList();
    private final ArchiveConnectionHandler _connectionHandler;

    /**
     * Constructor.
     * @param connectionHandler
     * @param name
     * @param periodInMS
     * @param provider
     */
    public SamplsPersistDataWorker(final ArchiveConnectionHandler connectionHandler,
                                   final String name,
                                   final long periodInMS,
                                   final IBatchQueueHandlerProvider provider) {
        super(connectionHandler, name, periodInMS, provider);
        _connectionHandler = connectionHandler;
        _name = name;
    }

    /**
     * Constructor.
     * @param connectionHandler
     * @param name
     * @param prefPeriodInMS
     * @param handler
     */
    public SamplsPersistDataWorker(final ArchiveConnectionHandler connectionHandler,
                                   final String name,
                                   final Integer prefPeriodInMS,
                                   @SuppressWarnings("rawtypes") final TypeSupport handler) {
        super(connectionHandler, name, prefPeriodInMS, handler);
        _connectionHandler = connectionHandler;
        _name = name;

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void measuredRun() {
        //   LOG.info( " {}",Thread.);
        try {
            processBatchHandler(_connectionHandler.getThreadLocalConnection(),
                                (BatchQueueHandlerSupport) getHandler(),
                                _rescueDataList);
        } catch (final ArchiveConnectionException e) {
            e.printStackTrace();
        }
        //   processBatchHandlers(_connectionHandler.getThreadLocalConnection(), _handlerProvider, _rescueDataList);

    }


}
