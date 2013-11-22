/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.notification.ArchiveNotifications;
import org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleBatchQueueHandler;
import org.csstudio.domain.desy.task.AbstractTimeMeasuredRunnable;
import org.csstudio.domain.desy.time.StopWatch;
import org.csstudio.domain.desy.time.StopWatch.RunningStopWatch;
import org.epics.pvmanager.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Persistence layer worker for batched statements.
 * Intended to be scheduled periodically and if necessary on demand
 * (when the queue is getting big or the contained statements reach the max allowed packet size).
 *
 * Gets a connection and does not close it! As this worker is expected to use it very very often.
 *
 * @author bknerr
 * @since 08.02.2011
 */
public class PersistDataWorker extends AbstractTimeMeasuredRunnable {

    private static final Logger RESCUE_LOG = LoggerFactory.getLogger("StatementRescueLogger");
    private static final Logger LOG = LoggerFactory.getLogger(PersistDataWorker.class);
    private static final Logger EMAIL_LOG = LoggerFactory.getLogger("ErrorPerEmailLogger");

    private final ArchiveConnectionHandler _connectionHandler;

    private final String _name;
    private final long _periodInMS;

    @SuppressWarnings("unused")
    private final IBatchQueueHandlerProvider _handlerProvider;
    private final List<Object> _rescueDataList = Lists.newLinkedList();
    private final RunningStopWatch _watch;
    @SuppressWarnings("rawtypes")
    private final TypeSupport _handler;

    /**
     * Constructor.
     */
    public PersistDataWorker(@Nonnull final ArchiveConnectionHandler connectionHandler,
                             @Nonnull final String name,
                             @Nonnull final long periodInMS,
                             @Nonnull final IBatchQueueHandlerProvider provider) {
        _connectionHandler = connectionHandler;
        _name = name;
        _periodInMS = periodInMS;
        _handlerProvider = provider;
        _watch = StopWatch.start();
        _handler = null;
    }

    /**
     * Constructor.
     * @param connectionHandler
     * @param name
     * @param prefPeriodInMS
     * @param handler
     */
    @SuppressWarnings("rawtypes")
    public PersistDataWorker(final ArchiveConnectionHandler connectionHandler,
                             final String name,
                             final Integer prefPeriodInMS,
                             final TypeSupport handler) {
        _connectionHandler = connectionHandler;
        _name = name;
        _periodInMS = prefPeriodInMS;
        _handlerProvider = null;
        _watch = StopWatch.start();
        _handler = handler;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void measuredRun() {
        try {
          //  LOG.info( " {}", Thread.currentThread().getId());
            processBatchHandler(_connectionHandler.getThreadLocalConnection(),(BatchQueueHandlerSupport) _handler, _rescueDataList);

        } catch (final Throwable t) {
            LOG.error("Unknown throwable in thread {}.", _name);
            t.printStackTrace();
            EMAIL_LOG.info("Unknown throwable in thread {}. See event.log for more info.", _name);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> void processBatchHandlers(@Nonnull final Connection connection,
                                            @Nonnull final IBatchQueueHandlerProvider handlerProvider,
                                            @Nonnull final List<Object> rescueDataList) {
        final Collection<T> elements = Lists.newLinkedList();

        for (final BatchQueueHandlerSupport<T> handler : handlerProvider.getHandlers()) {
            final BlockingQueue<T> queue = handler.getQueue();
            queue.drainTo(elements);
            handlerProcessBatchForStatement(connection, handler, rescueDataList, elements);
          }
    }

    protected <T> void processBatchForStatement(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                                @Nonnull final Collection<T> elements,
                                                @Nonnull final PreparedStatement stmt,
                                                @Nonnull final List<T> rescueDataList) {
        PreparedStatement myStmt = stmt;
        try {
            int size = 0;
            for (final T element : elements) {
                while (myStmt.getConnection() == null || myStmt == null || myStmt.isClosed()) {
                    myStmt = handler.createNewStatement(_connectionHandler.getThreadLocalConnection());
                }
                addElementToBatchAndRescueList(handler, myStmt, element, rescueDataList);

                // executeBatchAndClearListOnCondition(handler, myStmt, rescueDataList, 1000);
                size = rescueDataList.size();
                if (size >= 1000) {
                    try {
                        _watch.restart();
                        final int iii[] = stmt.executeBatch();
                        LOG.debug("{}", iii.length);
                        LOG.debug("{}ms for {}x {}", new Object[] { _watch.getElapsedTimeInMillis(), size,
                                                                   handler.getHandlerType().getSimpleName() });
                    } catch (final Throwable t) {
                        handler.getQueue().addAll(elements);
                        elements.clear();
                        //     handleThrowable(t, handler, rescueDataList);
                    } finally {
                        rescueDataList.clear();
                    }
                }

            }
            //  executeBatchAndClearListOnCondition(handler, myStmt, rescueDataList, 1);
            size = rescueDataList.size();
            if (size > 0) {
                try {
                    _watch.restart();
                    //   int iii[] = stmt.executeBatch();
                    final int iii[] = stmt.executeBatch();
                    LOG.debug("{}", iii.length);
                    //  stmt.execute();
                    // stmt.executeUpdate();
                           if (!(handler instanceof ArchiveSampleBatchQueueHandler)) {
                    LOG.info("{}ms for {}x {}", new Object[] { _watch.getElapsedTimeInMillis(), iii.length,
                                                              handler.getHandlerType().getSimpleName() });
                         }
                } catch (final Throwable t) {
                    handler.getQueue().addAll(elements);
                    elements.clear();
                    //  handleThrowable(t, handler, rescueDataList);
                } finally {
                    rescueDataList.clear();
                }
            }
        } catch (final Throwable t) {
            handler.getQueue().addAll(elements);
            elements.clear();
            LOG.warn("Update error {}", myStmt.toString());
            handleThrowable(t, handler, rescueDataList);
        } finally {
            closeStatement(myStmt);

        }
    }

    private <T> void addElementToBatchAndRescueList(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                                    @Nonnull final PreparedStatement stmt,
                                                    @Nonnull final T element,
                                                    @Nonnull final List<T> rescueDataList) throws ArchiveDaoException {
        rescueDataList.add(element);

        handler.applyBatch(stmt, element);

    }

    @Nonnull
    private <T> boolean executeBatchAndClearListOnCondition(@Nonnull final BatchQueueHandlerSupport<T> handler,
                                                            @Nonnull final PreparedStatement stmt,
                                                            @Nonnull final List<T> rescueDataList,
                                                            final int minBatchSize) throws SQLException {
        final int size = rescueDataList.size();
        if (size >= minBatchSize) {
            try {
                _watch.restart();
                final int iii[] = stmt.executeBatch();
                LOG.debug("{}", iii.length);

                //  stmt.execute();
                // stmt.executeUpdate();
                LOG.debug("{}ms for {}x {}", new Object[] { _watch.getElapsedTimeInMillis(), size,
                                                           handler.getHandlerType().getSimpleName() });
            } finally {
                rescueDataList.clear();
            }
            return true;
        }
        return false;
    }

    private <T> void handleThrowable(@Nonnull final Throwable t,
                                     @Nonnull final BatchQueueHandlerSupport<T> handler,
                                     @Nonnull final List<T> rescueDataList) {
        final Collection<String> statements = handler.convertToStatementString(rescueDataList);
        try {
            throw t;
        } catch (final ArchiveConnectionException se) {
            LOG.error("Archive Connection failed. No batch update. Drain unpersisted statements to file system.", se);
            rescueDataToFileSystem(statements);
        } catch (final BatchUpdateException be) {
            LOG.error("Batched update failed. Drain unpersisted statements to file system.", be);
            processFailedBatch(statements, be);
        } catch (final SQLException se) {
            LOG.error("Batched update failed. Batched statement could not be composed.", se);
            rescueDataToFileSystem(statements);
        } catch (final Throwable tt) {
            LOG.error("Unknown throwable. Thread " + _name + " is terminated", tt);
            rescueDataToFileSystem(statements);
        } finally {
            rescueDataList.clear();
        }
    }

    private <T> void processFailedBatch(@Nonnull final Collection<String> batchedStatements,
                                        @Nonnull final BatchUpdateException be) {
        // NOT all statements have been successfully executed! (Depends on RDBM)
        final int[] updateCounts = be.getUpdateCounts();
        if (updateCounts.length == batchedStatements.size()) {
            // All statements have been tried to be executed, look for the failed ones
            final List<String> failedStmts = findFailedStatements(updateCounts, batchedStatements);
            rescueDataToFileSystem(failedStmts);
        } else {
            // Not all statements have been tried to be executed - safe only the failed ones
            rescueDataToFileSystem(Iterables.skip(batchedStatements, updateCounts.length));
        }
    }

    private static void closeStatement(@CheckForNull final Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (final SQLException e) {
                LOG.warn("Closing of statement failed: " + stmt);
            }
        }
    }

    @Nonnull
    private static List<String> findFailedStatements(@Nonnull final int[] updateCounts,
                                                     @Nonnull final Collection<String> allStmts) {
        final List<String> failedStmts = Lists.newLinkedList();
        int i = 0;
        for (final String stmt : allStmts) {
            if (i < updateCounts.length && updateCounts[i] == Statement.EXECUTE_FAILED) {
                failedStmts.add(stmt);
            }
            i++;
        }
        return failedStmts;
    }

    @Nonnull
    public String getName() {
        return _name;
    }

    public long getPeriodInMS() {
        return _periodInMS;
    }

   void rescueDataToFileSystem(@Nonnull final Iterable<String> statements) {
        final int noOfRescuedStmts = Iterables.size(statements);
        LOG.warn("Rescue statements: " + noOfRescuedStmts);
        int no = 0;
        for (final String stmt : statements) {
            RESCUE_LOG.info(stmt);
            no++;
        }
        if (no != 0) {
            ArchiveNotifications.notify(NotificationType.PERSIST_DATA_FAILED, "#Rescued: " + no);
        }
    }

    @SuppressWarnings("rawtypes")
    public TypeSupport getHandler() {
        return _handler;
    }


    /**
     * @param connection
     * @param handlerProvider
     * @param rescueDataList
     */
    protected <T> void processBatchHandler(final Connection connection,
                                           final BatchQueueHandlerSupport<T>  handler,
                                           final List<Object> rescueDataList) {
        try {if(handler!=null){
            final Collection<T> elements = Lists.newLinkedList();
            final BlockingQueue<T> queue = handler.getQueue();
            if (queue.size() > new MySQLArchivePreferenceService().getQueueMaxiSize()) {
                for (; queue.size() > 0;) {
                    synchronized (queue) {
                        queue.drainTo(elements, 1000);
                        final Collection<String> statements = handler.convertToStatementString(elements);
                        elements.clear();
                        rescueDataToFileSystem(statements);
                    }

                }
            }
            queue.drainTo(elements);
            handlerProcessBatchForStatement(connection, handler, rescueDataList, elements);
        } } catch (final Throwable t) {
            LOG.error("Unknown throwable in thread {}.", _name);
            t.printStackTrace();
            EMAIL_LOG.info("Unknown throwable in thread {}. See event.log for more info.", _name);
            }

    }
    /**
     * @param connection
     * @param handlerProvider
     * @param rescueDataList
     */
    @SuppressWarnings("unchecked")
    protected <T> void handlerProcessBatchForStatement(Connection connection,
                                           final BatchQueueHandlerSupport<T>  handler,
                                           final List<Object> rescueDataList,
                                           final Collection<T> elements) {
        try {
                if (!elements.isEmpty()) {
                PreparedStatement stmt = null;
                try {//bei jedes Mal SQL Statement erzeugen, connection neue prüfen, ob die Connection closed ist
                    while (connection == null || connection.isClosed()) {
                        connection = _connectionHandler.getThreadLocalConnection();
                    }
                    stmt = handler.createNewStatement(connection);
                    processBatchForStatement(handler, elements, stmt, (List<T>) _rescueDataList);
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

        } catch (final Throwable t) {
            LOG.error("Unknown throwable in thread {}.", _name);
            t.printStackTrace();
            EMAIL_LOG.info("Unknown throwable in thread {}. See event.log for more info.", _name);
        }

    }
}
