/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.common.engine.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.dal2.dv.Characteristic;
import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Timestamp;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.IDalService;
import org.csstudio.dal2.service.IPvAccess;
import org.csstudio.dal2.service.IPvListener;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Base for archived channels.
 *
 *  @author Kay Kasemir
 *  @author Bastian Knerr
 *  @param <V> the basic value type
 *  @param <T> the system variable for the basic value type
 */
@SuppressWarnings("nls")
public class ArchiveChannelBuffer<V extends Serializable, T extends ISystemVariable<V>> {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveChannelBuffer.class);

    /** Channel name.
     *  This is the name by which the channel was created,
     *  not the PV name that might include decorations.
     */
    private final PvAddress _address;

    private final ArchiveChannelId _id;

    private String _datatype;

    /** Buffer of received samples, periodically written */
    private final SampleBuffer<V, T, IArchiveSample<V, T>> _buffer;

    /** Is this channel currently running?
     *  <p>
     *  PV sends another 'disconnected' event
     *  as the result of 'stop', but we don't
     *  want to log that, so we keep track of
     *  the 'running' state.
     */
    @GuardedBy("this")
    private boolean _isStarted;

    @GuardedBy("this")
    private boolean _isEnabled;

    /** Most recent value of the PV.
     *  <p>
     *  This is the value received from the PV,
     *  is is not necessarily written to the archive.
     *  <p>
     */
    @GuardedBy("this")
    private T _mostRecentSysVar;

    /**
     * Counter for received values (monitor updates)
     */
    private long _receivedSampleCount;

    private final IServiceProvider _provider;

    private final TimeInstant _timeOfLastSampleBeforeChannelStart;

    private IPvAccess<Object> _pvAccess;

    private IPvListener<Object> _listener;

    /**
     * Constructor.
     * @throws EngineModelException
     * @throws TypeSupportException
     */
    public ArchiveChannelBuffer(@Nonnull final IArchiveChannel cfg, @Nonnull final IServiceProvider provider) throws EngineModelException {

        _address = PvAddress.getValue(cfg.getName());
        _id = cfg.getId();
        _datatype = cfg.getDataType();

        _timeOfLastSampleBeforeChannelStart = cfg.getLatestTimestamp();
        _isEnabled = cfg.isEnabled();
        _buffer = new SampleBuffer<V, T, IArchiveSample<V, T>>(_address.getAddress());
        _provider = provider;
    }

    /** @return Name of channel */
    @Nonnull
    public String getName() {
        return _address.getAddress();
    }

    /** @return Short description of sample mechanism */
    @Nonnull
    public String getMechanism() {
        return "MONITOR (on change)";
    }

    /** @return <code>true</code> if connected */
    public boolean isConnected() {
        return _pvAccess != null && _pvAccess.isConnected();
    }

    /** @return <code>true</code> if connected */
    public boolean isStarted() {
        return _isStarted;
    }

    /** @return Human-readable info on internal state of PV */
    @CheckForNull
    public String getInternalState() {
        return "UNKNOWN via DAL2";
    }

    @CheckForNull
    public TimeInstant getTimeOfMostRecentSample() {
        return _mostRecentSysVar != null ? _mostRecentSysVar.getTimestamp() : _timeOfLastSampleBeforeChannelStart;
    }

    /**
     * Checks whether this channel is enabled for archiving and if so it is started.
     * @return true if the channel could be started, false otherwise
     * @throws EngineModelException
     */
    public boolean start(@Nonnull final String info) throws EngineModelException {
        synchronized (this) {
            if (_isStarted) {
                return true;
            }
            _isStarted = true;
            initPvAndListener(info);
        }
        try {
            enable();
        } catch (final EngineModelException e) {
            LOG.error("PV " + _address.getAddress() + " could not be enabled. Database access failed", e);
            throw e;
        }
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void initPvAndListener(@Nonnull final String info) throws EngineModelException {

        try {
            final IDalService dalService = _provider.getDalService();
            _pvAccess = dalService.getPVAccess(_address, Type.NATIVE, ListenerType.LOG);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Missing dynamic service", e);
        }

        _listener = new IPvListener<Object>() {

            private Double _graphMin;
            private Double _graphMax;

            @Override
            public void connectionChanged(final IPvAccess<Object> source, final boolean connected) {
                try {
                    persistChannelStatusInfo(_id, connected, info);
                } catch (final EngineModelException e) {
                    LOG.error("Error persisting channel status info", e);
                }

                try {
                    final Type<?> nativeType = source.getLastKnownNativeType();

                    String datatype = nativeType.getJavaType().getSimpleName();
                    if (nativeType.isSequenceType()) {
                        datatype = "ArrayList<" + datatype + ">";
                    }

                    if (!datatype.equals(_datatype)) {
                        final IArchiveEngineFacade service = _provider.getEngineFacade();
                        _datatype = datatype;
                        service.writeChannelDataTypeInfo(_id, _datatype);
                    }
                } catch (final OsgiServiceUnavailableException e) {
                    LOG.error("Error persisting channel type info", e);
                } catch (final ArchiveServiceException e) {
                    LOG.error("Error persisting channel type info", e);
                }
            }

            @Override
            public void valueChanged(final IPvAccess<Object> source, final Object value) {

                final Characteristics characteristics = source.getLastKnownCharacteristics();

                try {
                    final Double newGraphMin = characteristics.get(Characteristic.GRAPH_MIN);
                    final Double newGraphMax = characteristics.get(Characteristic.GRAPH_MAX);

                    boolean changed = false;
                    if (_graphMin == null && newGraphMin != null || _graphMin != null && !_graphMin.equals(newGraphMin)) {
                        _graphMin = newGraphMin;
                        changed = true;
                    }

                    if (_graphMax == null && newGraphMax != null || _graphMax != null && !_graphMax.equals(newGraphMax)) {
                        _graphMax = newGraphMax;
                        changed = true;
                    }

                    if (changed) {
                        IArchiveEngineFacade service = _provider.getEngineFacade();
                        service.writeChannelDisplayRangeInfo(_id, _graphMin, _graphMax);
                    }

                } catch (final OsgiServiceUnavailableException e) {
                    LOG.error("Error updating range info", e);
                } catch (final ArchiveServiceException e) {
                    LOG.error("Error updating range info", e);
                }

                // TODO Move this to characteristics: IAlarmm characteristics.get(Characteristic.ALARM)
                final EpicsAlarmStatus alarmStatus = characteristics.get(Characteristic.STATUS);
                final EpicsAlarmSeverity alarmSeverity = characteristics.get(Characteristic.SEVERITY);

                final EpicsAlarm alarm = new EpicsAlarm(alarmSeverity, alarmStatus);

                final Timestamp timestamp = characteristics.get(Characteristic.TIMESTAMP);

                final ControlSystem origin = ControlSystem.EPICS_DEFAULT;

                // TODO Is it possible to avoid this mapping? We could use TimeInstant within DAL2 ...
                final TimeInstant timeInstant = TimeInstant.TimeInstantBuilder.fromNanos(timestamp.toNanoTime());

                // TODO Should we provide a SystemVariable from DAL2?

                Type<?> type = source.getLastKnownNativeType();

                V data;
                if (type.isSequenceType()) {
                    Object[] valueAsArray = (Object[]) value;
                    data = (V) Arrays.asList(valueAsArray);
                } else {
                    data = (V) value;
                }

                String name = _address.getAddress();
                EpicsSystemVariable<V> systemVariable = new EpicsSystemVariable<V>(name, data, origin, timeInstant, alarm);

                ArchiveSample<V, T> sample;
                if (type.isSequenceType()) {
                    sample = new ArchiveMultiScalarSample(_id, systemVariable, alarm);
                } else {
                    sample = new ArchiveSample(_id, systemVariable, alarm);
                }

                // add sample to buffer
                synchronized (this) {
                    _receivedSampleCount++;
                    _mostRecentSysVar = sample.getSystemVariable();
                }


                if (!_buffer.add(sample)) {
                    ArchiveEngineSampleRescuer.with((Collection) Collections.singleton(sample)).rescue();
                }
            }

        };

        try {
            _pvAccess.registerListener(_listener);
        } catch (final DalException e) {
            throw new EngineModelException("Error connecting channel", e);
        }
    }

    public void enable() throws EngineModelException {
        synchronized (this) {
            if (isEnabled()) {
                return;
            }
            _isEnabled = true;
        }
        try {
            _provider.getEngineFacade().setEnableChannelFlag(_address.getAddress(), true);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Disabling of channel could not be persisted.", e);
        }
    }

    private void persistChannelStatusInfo(@Nonnull final ArchiveChannelId id,
                                          final boolean connected,
                                          @Nonnull final String info) throws EngineModelException {
        try {
            final IArchiveEngineFacade service = _provider.getEngineFacade();
            service.writeChannelStatusInfo(id, connected, info, TimeInstantBuilder.fromNow());
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable to handle channel connection info.", e);
        } catch (final ArchiveServiceException e) {
            throw new EngineModelException("Internal service error on handling channel connection info.", e);
        }
    }

    /**
     * Stop archiving this channel
     * @throws EngineModelException
     */
    public void stop(@Nonnull final String info) throws EngineModelException {
        synchronized (this) {
            if (!_isStarted) {
                return;
            }
            _isStarted = false;
        }
        persistChannelStatusInfo(_id, false, info);

        if (_pvAccess != null) {
            try {
                _provider.getDalService().dispose(_pvAccess);
            } catch (final OsgiServiceUnavailableException e) {
                throw new EngineModelException("Missing dynamic service", e);
            }
        }
    }

    public void disable() throws EngineModelException {

        stop("PERMANENT DISABLE");

        synchronized (this) {
            if (!isEnabled()) {
                return;
            }
            _isEnabled = false;
        }
        try {
            _provider.getEngineFacade().setEnableChannelFlag(_address.getAddress(), false);
        } catch (final OsgiServiceUnavailableException e) {
            throw new EngineModelException("Service unavailable. Disabling of channel could not be persisted.", e);
        }
    }

    @Nonnull
    public synchronized T getMostRecentSample() {
        return _mostRecentSysVar;
    }

    /** @return Count of received values */
    public synchronized long getReceivedValues() {
        return _receivedSampleCount;
    }

    /** @return Sample buffer */
    @Nonnull
    public final SampleBuffer<V, T, IArchiveSample<V, T>> getSampleBuffer() {
        return _buffer;
    }

    /** Reset counters */
    public void reset() {
        _buffer.statsReset();
        synchronized (this) {
            _receivedSampleCount = 0;
        }
    }

    @Override
    @Nonnull
    public String toString() {
        return "Channel " + getName() + ", " + getMechanism();
    }

    @Nonnull
    public ArchiveChannelId getId() {
        return _id;
    }

    public boolean isEnabled() {
        return _isEnabled;
    }
}
