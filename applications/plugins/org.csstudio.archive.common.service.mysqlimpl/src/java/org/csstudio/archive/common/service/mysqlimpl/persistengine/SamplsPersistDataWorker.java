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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * TODO (xuwenhu) :
 *
 * @author xuwenhu
 * @since 18.07.2013
 */
public class SamplsPersistDataWorker extends PersistDataWorker {
        private static final Logger RESCUE_LOG =
            LoggerFactory.getLogger("StatementRescueLogger");
        private static final Logger LOG =
                LoggerFactory.getLogger(SamplsPersistDataWorker.class);
        private static final Logger EMAIL_LOG =
            LoggerFactory.getLogger("ErrorPerEmailLogger");
        private final String _name;
        private final long _periodInMS;
        private final List<Object> _rescueDataList = Lists.newLinkedList();
        private final IBatchQueueHandlerProvider _handlerProvider;
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
        _connectionHandler=connectionHandler;
        _name = name;
        _periodInMS = periodInMS;
        _handlerProvider = provider;
        // TODO Auto-generated constructor stub
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void measuredRun() {

        LOG.info("Sample RUN");
        try {
          processBatchHandlers(_connectionHandler.getThreadLocalConnection(), _handlerProvider, _rescueDataList);

        } catch (final Throwable t) {
            LOG.error("Unknown throwable in thread {}.", _name);
            t.printStackTrace();
            EMAIL_LOG.info("Unknown throwable in thread {}. See event.log for more info.", _name);
        }

    }
    @Override
    @SuppressWarnings("unchecked")
    protected <T> void processBatchHandlers(@Nonnull Connection connection,
                                          @Nonnull final IBatchQueueHandlerProvider handlerProvider,
                                          @Nonnull final List<T> rescueDataList) {
        final Collection<T> elements = Lists.newLinkedList();

        for (final BatchQueueHandlerSupport<T> handler : handlerProvider.getHandlers()) {
            if(ArchiveSample.class.getSimpleName().equals( handler.getHandlerType().getSimpleName())) {
            final BlockingQueue<T> queue= handler.getQueue();
            if(queue.size()<200) {
                return;
            }
        if(queue.size()>new MySQLArchivePreferenceService().getQueueMaxiSize()){
            for(;queue.size()>0;){
               synchronized (queue) {
                   queue.drainTo(elements, 1000);
                   final Collection<String> statements = handler.convertToStatementString(elements);
                   elements.clear();
                   rescueDataToFileSystem(statements);

            }

           }
        }
            queue.drainTo(elements);
            if (!elements.isEmpty()) {
                PreparedStatement stmt = null;
                try {//bei jedes Mal SQL Statement erzeugen, connection neue prüfen, ob die Connection closed ist
                    while(connection==null || connection.isClosed() ) {
                        connection = _connectionHandler.getThreadLocalConnection();
                    }
                    stmt = handler.createNewStatement(connection);
                    processBatchForStatement(handler, elements, stmt, rescueDataList);
                } catch (final ArchiveConnectionException e) {
                    handler.getQueue().addAll(elements);
                    elements.clear();
                    LOG.error("Connection to archive failed", e);
                    // FIXME (bknerr) : strategy for queues getting full, when to rescue data? How to check for failover?
                } catch (final SQLException e) {
                    handler.getQueue().addAll(elements);
                    elements.clear();
                     LOG.error("Creation of batch statement failed for strategy " + handler.getClass().getSimpleName(), e);
                    // FIXME (bknerr) : strategy for queues getting full, when to rescue data?
                }
                elements.clear();
            }
        }}
    }


}
