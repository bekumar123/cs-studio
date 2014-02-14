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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.mysqlimpl.MySQLArchivePreferenceService;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.batch.IBatchQueueHandlerProvider;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.sample.ArchiveSampleBatchQueueHandler;
import org.csstudio.domain.desy.DesyRunContext;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.TypeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Manager that handles the persistence worker thread.
 * @author Bastian Knerr
 * @since Feb 26, 2011
 */
public class PersistEngineDataManager {


  /**wenhua 
     * new LOG for this class
  */
    private static final Logger LOG = LoggerFactory.getLogger(PersistEngineDataManager.class);
    // TODO (bknerr) : number of threads?
    // get no of cpus and expected no of archive engines, and available archive connections
    private final int _cpus = Runtime.getRuntime().availableProcessors();
    /**
     * The thread pool executor for the periodically scheduled workers.
     */
    @SuppressWarnings("unused")
    private final boolean haveSampleThread = false;
    private final Map<String, Boolean> batchQueueHandlerMap = new MapMaker().makeMap();
    private final ScheduledThreadPoolExecutor _executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);//Math.max(2, _cpus + 1)
    //jhatje 2.2.12: set to 1 Thread
    //jhatje 22.2.12: back to previous thread number
    //    private final ScheduledThreadPoolExecutor _executor =
    //            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
    
  /**wenhua 
     * new Executor for SamplesPersistDataWorker class
  */
    private final ScheduledThreadPoolExecutor _writeSamplesExecutor = (ScheduledThreadPoolExecutor) Executors
            .newScheduledThreadPool(10);
    /**
      * Sorted set for submitted periodic workers - decreasing by period
      */
    private final SortedSet<PersistDataWorker> _submittedWorkers = Sets.newTreeSet(new Comparator<PersistDataWorker>() {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(@Nonnull final PersistDataWorker arg0, @Nonnull final PersistDataWorker arg1) {
            return Long.valueOf(arg0.getPeriodInMS()).compareTo(Long.valueOf(arg1.getPeriodInMS()));
        }
    });
    private final AtomicInteger _workerId = new AtomicInteger(0);

    private final Integer _prefPeriodInMS;
    private final Integer _prefTermTimeInMS;

    private final ArchiveConnectionHandler _connectionHandler;

    private final IBatchQueueHandlerProvider _allHandlersProvider = new IBatchQueueHandlerProvider() {
        @SuppressWarnings("rawtypes")
        @Override
        @Nonnull
        public Collection<BatchQueueHandlerSupport> getHandlers() {
            return BatchQueueHandlerSupport.getInstalledHandlers();
        }
    };

    /**
     * Constructor.
     */
    @Inject
    public PersistEngineDataManager(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                    @Nonnull final MySQLArchivePreferenceService prefs) {
        _connectionHandler = connectionHandler;

        _prefPeriodInMS = prefs.getPeriodInMS();
        _prefTermTimeInMS = prefs.getTerminationTimeInMS();

        addGracefulShutdownHook(_connectionHandler, _allHandlersProvider, _prefTermTimeInMS);
    }

    /*  private void submitNewPersistDataWorker(@Nonnull final ScheduledThreadPoolExecutor executor,
                                              @Nonnull final ArchiveConnectionHandler connectionHandler,
                                              @Nonnull final Integer prefPeriodInMS,
                                              @Nonnull final IBatchQueueHandlerProvider handlerProvider,
                                              @Nonnull final AtomicInteger workerId,
                                              @Nonnull final SortedSet<PersistDataWorker> submittedWorkers) {

          final PersistDataWorker newWorker =
                                              new PersistDataWorker(connectionHandler,
                                                                    "PERIODIC Worker: " + workerId.getAndIncrement(),
                                                                    prefPeriodInMS,
                                                                    handlerProvider);

        //  executor.scheduleAtFixedRate(newWorker, 0L, newWorker.getPeriodInMS() * 2, TimeUnit.MILLISECONDS);
              executor.scheduleWithFixedDelay(newWorker,
                                          1L,
                                          newWorker.getPeriodInMS()*2,
                                          TimeUnit.MILLISECONDS);


          submittedWorkers.add(newWorker);
          if (!haveSampleThread) {
              final SamplsPersistDataWorker newWorker1 =
                                                         new SamplsPersistDataWorker(connectionHandler,
                                                                                     "PERIODIC Worker: "
                                                                                             + workerId.getAndIncrement(),
                                                                                     prefPeriodInMS,
                                                                                     handlerProvider);
            //  _writeSamplesExecutor.scheduleAtFixedRate(newWorker1, 0L, 1000, TimeUnit.MILLISECONDS);
                _writeSamplesExecutor.scheduleWithFixedDelay(newWorker1,
                                  1L,
                                  prefPeriodInMS,
                                  TimeUnit.MILLISECONDS);
              submittedWorkers.add(newWorker1);
              haveSampleThread = true;
          }
      }
    */
   /**wenhua 
     * new method for PersistDataWorker  with handler for ChannelGroup
  */
    @SuppressWarnings("rawtypes")
    private void submitNewPersistDataWorker(@Nonnull final ScheduledThreadPoolExecutor executor,
                                            @Nonnull final ArchiveConnectionHandler connectionHandler,
                                            @Nonnull final Integer prefPeriodInMS,
                                            @Nonnull final TypeSupport handler,
                                            @Nonnull final AtomicInteger workerId,
                                            @Nonnull final SortedSet<PersistDataWorker> submittedWorkers) {

        if (handler instanceof ArchiveSampleBatchQueueHandler) {
            final SamplsPersistDataWorker newWorker1 =
                                                       new SamplsPersistDataWorker(connectionHandler,
                                                                                   "PERIODIC Worker: "
                                                                                           + workerId.getAndIncrement(),
                                                                                   prefPeriodInMS,
                                                                                   handler);
            //   _writeSamplesExecutor.scheduleAtFixedRate(newWorker1, 0L, 1000, TimeUnit.MILLISECONDS);
            executor.scheduleWithFixedDelay(newWorker1, 1L, 1000, TimeUnit.MILLISECONDS);
            submittedWorkers.add(newWorker1);
            LOG.warn("new Thread for {} start", handler.getClass().getSimpleName());

        } else {
            final PersistDataWorker newWorker =
                                                new PersistDataWorker(connectionHandler,
                                                                      "PERIODIC Worker: " + workerId.getAndIncrement(),
                                                                      prefPeriodInMS,
                                                                      handler);

            //  executor.scheduleAtFixedRate(newWorker, 0L, newWorker.getPeriodInMS() * 2, TimeUnit.MILLISECONDS);
            executor.scheduleWithFixedDelay(newWorker, 1L, newWorker.getPeriodInMS() * 2, TimeUnit.MILLISECONDS);

            submittedWorkers.add(newWorker);
            LOG.warn("new Thread for {} start", handler.getClass().getSimpleName());

        }
    }

    /**
     * This shutdown hook is only added when the sys property context is not set to "CI",
     * meaning continuous integration. This is a flaw as the production code should be unaware
     * of its run context, but we couldn't think of another option.
     * @param prefTermTimeInMS
     */
    private void addGracefulShutdownHook(@Nonnull final ArchiveConnectionHandler connectionHandler,
                                         @Nonnull final IBatchQueueHandlerProvider provider,
                                         @Nonnull final Integer prefTermTimeInMS) {
        if (DesyRunContext.isProductionContext()) {
            /**
             * Add shutdown hook.
             */
            Runtime.getRuntime().addShutdownHook(new ShutdownWorkerThread(connectionHandler, provider, prefTermTimeInMS));
        }
    }

    /**
     * Checks whether we need another worker.
     * First check is whether the blocking queue of statements exceeds the max allowed packet size.
     * If so, is there still space in the thread pool for another periodic task.
     * If not so, is there the possibility to replace a rarely scheduled task with a task with higher
     * frequency.
     * If not so, FIXME (bknerr) : start a data rescue worker to save the stuff to disc and inform the staff per email
     * @return
     */
    private boolean isAnotherWorkerRequired() {
        if (noWorkerPresentYet()) {
            return true;
        }
        //
        //
        //
        //        if (isMaxPoolSizeNotReached()) {
        //            submitNewPersistDataWorker(_executor,
        //                                       _prefPeriodInMS,
        //                                       _allHandlersProvider,
        //                                       _workerId,
        //                                       _submittedWorkers);
        //        } else {
        //            lowerPeriodOfExistingWorker(_executor,
        //                                        _prefPeriodInMS,
        //                                        _allHandlersProvider,
        //                                        _workerId,
        //                                        _submittedWorkers);
        //        }

        return false;
    }

    private boolean noWorkerPresentYet() {
        return _executor.getPoolSize() <= 0;
    }

    //    private boolean isMaxPoolSizeNotReached() {
    //        return _executor.getPoolSize() < _executor.getCorePoolSize();
    //    }
    //    private void lowerPeriodOfExistingWorker(ScheduledThreadPoolExecutor executor,
    //                                             Integer prefPeriodInMS,
    //                                             IBatchQueueHandlerProvider handlerProvider,
    //                                             AtomicInteger workerId,
    //                                             SortedSet<PersistDataWorker> submittedWorkers) {
    //
    //    }

    //
    //    private boolean isPeriodAlreadySetToMinimum(final long period) {
    //        return Long.valueOf(period).intValue() <= 2000;
    //    }
    //
    //    private void handlePoolExhaustionWithMinimumPeriodCornerCase() {
    //        // FIXME (bknerr) : handle pool and thread frequency exhaustion
    //        // notify staff, rescue data to disc with dedicated worker
    //    }
    //
    //    private void lowerPeriodAndRemoveOldestWorker(@Nonnull final Iterator<PersistDataWorker> it,
    //                                                  @Nonnull final PersistDataWorker oldestWorker) {
    //        _prefPeriodInMS = Math.max(_prefPeriodInMS>>1, 2000);
    //        LOG.info("Remove Worker: " + oldestWorker.getName());
    //        _executor.remove(oldestWorker);
    //        it.remove();
    //    }

    @Nonnull
    public ArchiveConnectionHandler getConnectionHandler() {
        return _connectionHandler;
    }

    public void shutdown() {
        _executor.shutdown();
    }
  /**wenhua 
     * PersistDataWorker  with handler for ChannelGroup
  */
    @SuppressWarnings("rawtypes")
    public int submitToBatch(@Nonnull final Collection<?> entries) throws TypeSupportException {

        final int size = BatchQueueHandlerSupport.addToQueue(entries);
        final Class type = entries.iterator().next().getClass();
        @SuppressWarnings({ "unchecked" })
        final TypeSupport support =
                                    BatchQueueHandlerSupport.findTypeSupportForOrThrowTSE(BatchQueueHandlerSupport.class,
                                                                                          type);
        synchronized (batchQueueHandlerMap) {
            if (batchQueueHandlerMap.isEmpty() || batchQueueHandlerMap.get(support.getClass().getSimpleName()) == null
                || !batchQueueHandlerMap.get(support.getClass().getSimpleName())) {
                batchQueueHandlerMap.put(support.getClass().getSimpleName(), Boolean.TRUE);
                /* submitNewPersistDataWorker(_executor,
                                            _connectionHandler,
                                            _prefPeriodInMS,
                                            _allHandlersProvider,
                                            _workerId,
                                            _submittedWorkers);
                                            */
                submitNewPersistDataWorker(_executor,
                                           _connectionHandler,
                                           _prefPeriodInMS,
                                           support,
                                           _workerId,
                                           _submittedWorkers);
            }
        }

        return size;
    }
}
