/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channelgroup.IArchiveChannelGroup;
import org.csstudio.archive.common.service.engine.IArchiveEngine;
import org.csstudio.archive.common.service.enginestatus.ArchiveEngineStatus;
import org.csstudio.archive.common.service.enginestatus.EngineMonitorStatus;
import org.csstudio.archive.common.service.enginestatus.IArchiveEngineStatus;
import org.csstudio.domain.desy.epics.name.EpicsChannelName;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.IAlarmSystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;

/** Data model of the archive engine.
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 */
public final class EngineModel {
    private static final Logger LOG = LoggerFactory.getLogger(EngineModel.class);

    /** Name of this model */
    private final String _name;

    /** Executor that handles thread with WriteWorker */
    private WriteExecutor _writeExecutor;

    /**  All channels */
    private final ConcurrentMap<String, ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>>> _channelMap;

    /** Groups of archived channels */
    private final ConcurrentMap<String, ArchiveGroup> _groupMap;

    /** Engine start state */
    @GuardedBy("this")
    private volatile EngineState _state = EngineState.IDLE;

    /** Start time of the model */
    private TimeInstant _startTime;

    private final long _writePeriodInMS;
    /* wenhua xu
    _regGroupId
   */
    private final int _regGroupId;;
    private final long _heartBeatPeriodInMS;

    private final IServiceProvider _provider;
    private IArchiveEngine _engine;

    /**
     * Constructor
     */
    public EngineModel(@Nonnull final String engineName,
                       @Nonnull final IServiceProvider provider) throws EngineModelException {
        _name = engineName;
        _provider = provider;

        _groupMap = new MapMaker().concurrencyLevel(2).makeMap();
        _channelMap = new MapMaker().concurrencyLevel(2).makeMap();
        /* wenhua xu
        init variable value
       */
        _writePeriodInMS = 1000 * provider.getPreferencesService().getWritePeriodInS();
        _heartBeatPeriodInMS = 1000 * provider.getPreferencesService().getHeartBeatPeriodInS();
        _regGroupId = provider.getPreferencesService().getRegGroupId().intValue();
        _engine = EngineModelConfigurator.findEngineConfByName(_name, _provider);

        logHttpHostAndPort(_engine);
    }

    private void logHttpHostAndPort(@Nonnull final IArchiveEngine engine) {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (final UnknownHostException e) {
            hostName = "localhost";
        }
        LOG.info("Http server on host {} and port: {}", hostName, engine.getUrl().getPort());
    }

    /** @return Engine name (descriptive) */
    @Nonnull
    public String getName() {
        return _name;
    }

    /** @return Write period in milliseconds */
    public long getWritePeriodInMS() {
        return _writePeriodInMS;
    }

    /** @return Current model state */
    @Nonnull
    public synchronized EngineState getState() {
        return _state;
    }

    public synchronized void setState(@Nonnull final EngineState state) {
        _state = state;
    }

    /** @return Start time of the engine or <code>null</code> if not running */
    @CheckForNull
    public TimeInstant getStartTime() {
        return _startTime;
    }

    /**
     *  Add new group if not already exists.
     *
     *  @param _name Name of the group to find or add.
     *  @return ArchiveGroup the already existing or, if not, newly added group
     */
    @Nonnull
    public ArchiveGroup addGroup(@Nonnull final IArchiveChannelGroup groupCfg) {
        final String groupName = groupCfg.getName();
        _groupMap.putIfAbsent(groupName, new ArchiveGroup(groupCfg.getId(), groupName));
        return _groupMap.get(groupName);
    }

    /** @return Group by that name or <code>null</code> if not found */
    @CheckForNull
    public ArchiveGroup getGroup(@Nonnull final String name) {
        return _groupMap.get(name);
    }

    @Nonnull
    public Collection<ArchiveGroup> getGroups() {
        return _groupMap.values();
    }

    /** @return Channel by that name or <code>null</code> if not found */
    @CheckForNull
    public ArchiveChannelBuffer<?, ?> getChannel(@Nonnull final String name) {
        return _channelMap.get(name);
    }

    /** @return All channels */
    @Nonnull
    public Collection<ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>>> getChannels() {
        return _channelMap.values();
    }

    /**
     * Start processing all channels and writing to archive.
     * @throws EngineModelException
     */
    public void start() throws EngineModelException {
        if (getState() != EngineState.CONFIGURED) {
            throw new IllegalStateException("Engine has not been configured before start.", null);
        }
        if (_engine == null) {
            throw new IllegalStateException("Engine model is null although in state " + EngineState.CONFIGURED.name(), null);
        }
        _startTime = TimeInstantBuilder.fromNow();
        setState(EngineState.RUNNING);

        checkAndUpdateLastShutdownStatus(_provider, _engine, _channelMap.values());
        startChannelGroups(_groupMap.values());
          /* wenhua xu
       WriteExecutor starte Writethread for eache channel Group
       */
        _writeExecutor = new WriteExecutor(_provider, _engine.getId(), _channelMap.values(), this);
        _writeExecutor.start(_heartBeatPeriodInMS, _writePeriodInMS);
        LOG.info("Engine {} Start",getName());

    }

    /**
     * Retrieves the last archiver status from the archive.<br/>
     * If it was a graceful shutdown, anything's fine.<br/>
     * Otherwise:
     * <ol>
     * <li> update the engine_status table by an 'engine OFF' info with the timestamp of the
     * last engine.alive value. </li>
     * <li> update the channel_status table for all channels of this engine that have status
     * 'connected' with a new row disconnected and the timestamp of the last engine.alive value.</li>
     * </ol>
     * @param collection
     *
     * @throws EngineModelException
     */
    private void checkAndUpdateLastShutdownStatus(@Nonnull final IServiceProvider provider,
                                                  @Nonnull final IArchiveEngine engine,
                                                  @Nonnull final Collection<ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>>> channels) throws EngineModelException {
        try {
            final IArchiveEngineFacade facade = provider.getEngineFacade();

            final IArchiveEngineStatus engineStatus =
                facade.getLatestEngineStatusInformation(engine.getId(),
                                                        TimeInstantBuilder.fromNow());

            if (isNotFirstStart(engineStatus) && wasNotGracefullyShutdown(engineStatus)) {
                facade.writeEngineStatusInformation(engine.getId(),
                                                    EngineMonitorStatus.OFF,
                                                    engine.getLastAliveTime(),
                                                    "Ungraceful shutdown");

                checkAndUpdateChannelsStatus(facade, engine, channels);
            }
            facade.writeEngineStatusInformation(engine.getId(),
                                                EngineMonitorStatus.ON,
                                                TimeInstantBuilder.fromNow(),
                                                "Engine Startup");
        } catch (@Nonnull final Exception e) {
            e.printStackTrace();
            handleExceptions(e);
        }
    }

    private boolean wasNotGracefullyShutdown(@Nonnull final IArchiveEngineStatus engineStatus) {
        return !EngineMonitorStatus.OFF.equals(engineStatus.getStatus());
    }

    private boolean isNotFirstStart(@CheckForNull final IArchiveEngineStatus engineStatus) {
        return engineStatus != null;
    }

    /**
     * Looks up the latest channels' status in the 1 week interval before the latest engine's heartbeat.
     * Since the archive engine is not supposed to terminate ungracefully and then not be restarted within a
     * few days, it is reasonable to provide this information via the interface, whether the layer below
     * may use it or not. (infact, it is used and speeds up things drastically)
     * @param facade
     * @param engine
     * @param channels
     * @throws ArchiveServiceException
     */
    @SuppressWarnings("rawtypes")
    private void checkAndUpdateChannelsStatus(@Nonnull final IArchiveEngineFacade facade,
                                              @Nonnull final IArchiveEngine engine,
                                              @Nonnull final Collection<ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>>> channels) {
        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        final Runnable worker = new Runnable() {
            @Override
            public void run() {
                try {
                	 final TimeInstant latestAliveTime = engine.getLastAliveTime();
                      //write all channels status at latestAliveTime  as  "Ungraceful engine shutdown"
                     for (final ArchiveChannelBuffer ch : channels) {
                         facade.writeChannelStatusInfo(ch.getId(),
                                                       false,
                                                       "Ungraceful engine shutdown",
                                                       latestAliveTime);
                     }
                } catch (final ArchiveServiceException e) {
                    LOG.error("Exception within service impl. Archive engine heart beat persistence failed.", e);
                } catch (final Throwable t) {
                    LOG.error("Unknown throwable thread HeartBeatWorker.", t);
                }
            }
        };
        executor.submit(worker);

    }

    private void startChannelGroups(@Nonnull final Collection<ArchiveGroup> groups) throws EngineModelException {
        for (final ArchiveGroup group : groups) {
            group.start(ArchiveEngineStatus.ENGINE_START);
            if (getState() == EngineState.SHUTDOWN_REQUESTED) {
                break;
            }
        }
    }

    /** @return Timestamp of end of last write run or <code>null</code> if not started before */
    @CheckForNull
    public TimeInstant getLastWriteTime() {
        return _writeExecutor.getLastWriteTime();
    }

    /** @return Average number of values per write run */
    @CheckForNull
    public Double getAvgWriteCount() {
        return _writeExecutor.getAvgWriteCount();
    }

    /** @return  Average duration of write run in milliseconds */
    @CheckForNull
    public Duration getAvgWriteDuration() {
        return _writeExecutor.getAvgWriteDurationInMS();
    }

    /** Setting the model state to shutdown.
     *  @see #getState()
     */
    public synchronized void requestShutdown() {
        setState(EngineState.SHUTDOWN_REQUESTED);
    }

    /** Setting the model state to restart.
     *  @see #getState()
     */
    public synchronized void requestRestart() {
        setState(EngineState.RESTART_REQUESTED);
    }

    /** Reset engine statistics */
    public void resetStats() {
        _writeExecutor.reset();
        synchronized (this) {
            for (final ArchiveChannelBuffer<?, ?> channel : _channelMap.values()) {
                channel.reset();
            }
        }
    }

    /**
     * Stop monitoring the channels, flush the write buffers.
     * @throws EngineModelException
     */
    @SuppressWarnings("nls")
    public void stop() throws EngineModelException {
        final EngineState state = getState();
        if (state == EngineState.STOPPING || state == EngineState.IDLE) {
            return;
        }
        setState(EngineState.STOPPING);

        LOG.info("Stopping archive groups");
        for (final ArchiveGroup group : _groupMap.values()) {
            group.stop(ArchiveEngineStatus.ENGINE_STOP);
        }
        try {
            _provider.getEngineFacade().writeEngineStatusInformation(_engine.getId(),
                                                                     EngineMonitorStatus.OFF,
                                                                     TimeInstantBuilder.fromNow(),
                                                                     "Graceful Shutdown");
        } catch (final Exception e) {
            handleExceptions(e);
        }

        LOG.info("Shutting down writer");
        _writeExecutor.shutdown();

        setState(EngineState.IDLE);
    }

    /** Read configuration of model from RDB.
     *  @param port Current HTTPD port
     * @throws EngineModelException
     */
    @SuppressWarnings("nls")
    public void readConfigurationAndSetupGroupsAndChannels() throws EngineModelException {
        try {
            final EngineState state = getState();
            if (state != EngineState.IDLE) {
                LOG.error("Read configuration while state " + state + ". Should be " + EngineState.IDLE);
                return;
            }

            if (_engine == null) { // has been stopped before, then to be reconfigured
                _engine = EngineModelConfigurator.findEngineConfByName(_name, _provider);
            }

            final Collection<IArchiveChannelGroup> groups =
                                                            EngineModelConfigurator.findGroupsForEngine(_engine.getId(),
                                                                                                        _provider);

            for (final IArchiveChannelGroup groupCfg : groups) {
            	  /* wenhua xu
                   _regGroupId
                  */
                if (_regGroupId > 0 && groupCfg.getId().intValue() == _regGroupId) {
                    final ArchiveGroup group = addGroup(groupCfg);
                    EngineModelConfigurator.configureGroup(_provider, group, _channelMap);
                }
                if (_regGroupId < 0 && groupCfg.getId().intValue() != -_regGroupId) {
                    final ArchiveGroup group = addGroup(groupCfg);
                    EngineModelConfigurator.configureGroup(_provider, group, _channelMap);
                }
                if (_regGroupId == 0) {
                    final ArchiveGroup group = addGroup(groupCfg);
                    EngineModelConfigurator.configureGroup(_provider, group, _channelMap);
                }
            }
        } catch (final Exception e) {
            handleExceptions(e);
        }
        setState(EngineState.CONFIGURED);
    }

    private void handleExceptions(@Nonnull final Exception inE) throws EngineModelException {
        final String msg = "Failure during archive engine configuration retrieval: ";
        try {
            throw inE;
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException(msg + "Service unavailable.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException(msg + "Internal service exception.", e);
        } catch (final MalformedURLException e) {
            throw new EngineModelException(msg + "Engine url malformed.", e);
        } catch (final TypeSupportException e) {
            throw new EngineModelException(msg + "Channel type not supported.", e);
        } catch (final EngineModelException eme) {
            throw eme;
        } catch (final Exception re) {
            throw new EngineModelException("Unknown exception: ", re);
        }
    }

    public void clearConfiguration() {
        if (!(getState() == EngineState.IDLE || getState() == EngineState.CONFIGURED)) {
            throw new IllegalStateException("Clearing configuration only allowed in " + EngineState.IDLE.name() + " or "
                                            + EngineState.CONFIGURED.name() + " state.");
        }
        _engine = null;
        _groupMap.clear();
        _channelMap.clear();
        _startTime = null;

        setState(EngineState.IDLE);
    }

    @CheckForNull
    public Integer getHttpPort() {
        if (_engine != null) {
            return _engine.getUrl().getPort();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>> configureNewChannel(@Nonnull final EpicsChannelName epicsName,
                                                                                                 @Nonnull final String groupName,
                                                                                                 @Nullable final String type,
                                                                                                 @Nullable final String low,
                                                                                                 @Nullable final String high) throws EngineModelException {

        ArchiveChannelBuffer<?, ?> channelBuffer = getChannel(epicsName.toString());
        if (channelBuffer != null) {
            throw new EngineModelException("Channel with name: '" + epicsName.toString()
                                           + "' does already exist for this engine.", null);
        }
        final ArchiveGroup group = getGroup(groupName);
        if (group == null) {
            throw new EngineModelException("Group with name: '" + groupName + "' does not yet exist for this engine.", null);
        }
        channelBuffer =
                        EngineModelConfigurator.configureNewChannel(epicsName,
                                                                    type,
                                                                    low,
                                                                    high,
                                                                    group,
                                                                    _channelMap,
                                                                    _provider);
        return (ArchiveChannelBuffer<Serializable, IAlarmSystemVariable<Serializable>>) channelBuffer;
    }

    public void removeChannelFromConfiguration(@Nonnull final String name) throws EngineModelException {
        final ArchiveChannelBuffer<?, ?> buffer = getChannel(name);
        if (buffer == null) {
            throw new EngineModelException("Channel '" + name.toString() + "' is unknown!", null);
        }
        if (buffer.getTimeOfMostRecentSample() != null) {
            throw new EngineModelException("Removal of channel '"
                                                   + name.toString()
                                                   + "' not possible!"
                                                   + "\nThere are archived samples for this channel. Do you just like to stop archiving the channel?",
                                           null);
        }
        buffer.stop("STOP FOR REMOVAL");
        EngineModelConfigurator.removeChannel(name, _channelMap, _groupMap.values(), _provider);
    }

    public void configureNewGroup(@Nonnull final String name, @CheckForNull final String desc) throws EngineModelException {
        final ArchiveGroup group = getGroup(name);
        if (group != null) {
            throw new EngineModelException("Group '" + name + "' does already exist!", null);
        }
        final IArchiveChannelGroup archGroup =
                                               EngineModelConfigurator.configureNewGroup(name,
                                                                                         _engine.getId(),
                                                                                         desc,
                                                                                         _provider);
        addGroup(archGroup);
    }
}
