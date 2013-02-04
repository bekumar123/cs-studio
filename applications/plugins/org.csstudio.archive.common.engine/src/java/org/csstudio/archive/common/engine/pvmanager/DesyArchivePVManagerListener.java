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
package org.csstudio.archive.common.engine.pvmanager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.engine.model.ArchiveChannelBuffer;
import org.csstudio.archive.common.engine.model.ArchiveEngineSampleRescuer;
import org.csstudio.archive.common.engine.model.EngineModelException;
import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.epics.types.EpicsGraphicsData;
import org.csstudio.domain.desy.epics.types.EpicsMetaData;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for creating archive samples per value update.
 *
 * @author bknerr
 * @since 30.08.2011
 * @param <V> the basic element type of the variable's value collection
 * @param <T> the generic system variable type
 */
// CHECKSTYLE OFF: AbstractClassName
public abstract class DesyArchivePVManagerListener<V extends Serializable,
                                                   T extends ISystemVariable<V>> implements PVReaderListener {
// CHECKSTYLE ON: AbstractClassName

    private static final Logger LOG = LoggerFactory.getLogger(DesyArchivePVManagerListener.class);
    private static final Logger EMAIL_LOG = LoggerFactory.getLogger("ErrorPerEmailLogger");

    private final ArchiveChannelBuffer<V, T> _buffer;
    private IServiceProvider _provider;
    private final String _channelName;
    private final ArchiveChannelId _channelId;
    private String _datatype;
    private volatile boolean _firstConnection;

    private String _startInfo;
    @SuppressWarnings("unused")
    private String _stopInfo;
    private volatile boolean _isConnected;
    private final PVReader<?> _reader;
    /**
     * Constructor.
     */
    public DesyArchivePVManagerListener(@Nonnull final ArchiveChannelBuffer<V, T> buffer,
                                        @Nonnull final PVReader<?> reader,
                                        @Nonnull final IServiceProvider provider,
                                        @Nonnull final String name,
                                        @Nonnull final ArchiveChannelId id,
                                        @Nullable final String datatype) {
        _buffer = buffer;
        _reader = reader;
        _provider = provider;
        _channelName = name;
        _channelId = id;
        _datatype = datatype;
        _firstConnection = true;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void pvChanged() {
        try {
            // TODO (bknerr) : ask Gabriele whether it is a good choice to separate the value
            // update callback from the calling/value providing instance

            final List<EpicsSystemVariable> sysVars = (List<EpicsSystemVariable>) _reader.getValue();
            if(sysVars==null || sysVars.isEmpty()) {
                return;
            }
            if (_firstConnection && !sysVars.isEmpty()) {

                handleOnConnectionInformation(_provider, sysVars.get(0), _channelId, _buffer.isConnected(), _startInfo);
                _isConnected = _buffer.isConnected();
                _firstConnection = false;
            }
            //connectionsstate of the Channel
          if (!_firstConnection && _isConnected != _buffer.isConnected()) {

                _isConnected = _buffer.isConnected();

                handleOnConnectionInformation(_provider,
                                              sysVars.get(0),
                                              _channelId,
                                              _isConnected,
                                              _startInfo);
            }
            for (final EpicsSystemVariable sysVar : sysVars) {
                handleValueUpdateInformation(sysVar);
            }
        } catch (final Throwable t) {
            LOG.error("Unexpected exception in PVListener for: {}:\n{}", _channelName, t.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private void handleOnConnectionInformation(@Nonnull final IServiceProvider provider,
                                               @Nonnull final EpicsSystemVariable pv,
                                               @Nonnull final ArchiveChannelId id,
                                               final boolean connected,
                                               @Nonnull final String info)
                                               throws EngineModelException, OsgiServiceUnavailableException, ArchiveServiceException {
        _buffer.persistChannelStatusInfo(id, connected, info);

        if(!validateAndPersistDatatype(provider, id, pv.getData())) {
            _buffer.stop("Datatype mismatch");
            return;
        }

        final EpicsMetaData metaData = pv.getMetaData();
        if (metaData != null) {
            handleMetaDataInfo(id, metaData);
        }
    }

    private boolean validateAndPersistDatatype(@Nonnull final IServiceProvider provider,
                                               @Nonnull final ArchiveChannelId id,
                                               @Nonnull final Object data) throws OsgiServiceUnavailableException,
                                                                                     ArchiveServiceException {


        final String actualTypeFromData = ArchiveTypeConversionSupport.createArchiveTypeStringFromData(data);

        if (_datatype == null) {
            _datatype = actualTypeFromData;
            final IArchiveEngineFacade service = provider.getEngineFacade();
            service.writeChannelDataTypeInfo(id, actualTypeFromData);
        } else if (!_datatype.equals(actualTypeFromData)) {
            final String msg = "Datatype mismatch for channel: " + _channelName +
                               ". Expected from configuration is " + _datatype +
                               " which is not assignable from datatype " + actualTypeFromData + " of first received value.";
            LOG.info(msg);
            EMAIL_LOG.info(msg);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @CheckForNull
    private <W extends Comparable<? super W> & Serializable>
    EpicsMetaData handleMetaDataInfo(@Nonnull final ArchiveChannelId id,
                                     @Nonnull final EpicsMetaData data)
                                     throws EngineModelException {

       final EpicsGraphicsData<W> grData = (EpicsGraphicsData<W>) data.getGrData();
       if (grData != null) {
            try {
                final IArchiveEngineFacade service = _provider.getEngineFacade();
                service.writeChannelDisplayRangeInfo(id,
                                                     grData.getLowOperatingRange(),
                                                     grData.getHighOperatingRange());
            } catch (final OsgiServiceUnavailableException e) {
                throw new EngineModelException("Service unavailable on updating display range info.", e);
            } catch (final ArchiveServiceException e) {
                throw new EngineModelException("Internal service error on updating display range info.", e);
            }
        }
       return data;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void handleValueUpdateInformation(@Nonnull final EpicsSystemVariable pv) {
        if(pv==null) {
            return;
        }
        final ArchiveSample<V, T> sample = createSampleFromValue(pv, _channelId);
        if (sample == null || sample.getValue() == null) {
            return;
        }
        if (!addSampleToBuffer(sample)) {
            ArchiveEngineSampleRescuer.with((Collection) Collections.singleton(sample)).rescue();
        }
    }

    protected abstract boolean addSampleToBuffer(@Nonnull final IArchiveSample<V, T> sample);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @CheckForNull
    private ArchiveSample<V, T> createSampleFromValue(@Nonnull final EpicsSystemVariable sv,
                                                      @Nonnull final ArchiveChannelId id) {
        ArchiveSample<V, T> sample;
        if (Collection.class.isAssignableFrom(sv.getData().getClass())) {
            sample = new ArchiveMultiScalarSample(id, sv, sv.getAlarm());
        } else {
            sample = new ArchiveSample(id, sv, sv.getAlarm());
        }
        return sample;
    }


    public void setStartInfo(@Nonnull final String info) {
        _startInfo = info;

    }

    public void setStopInfo(@Nonnull final String info) {
        _stopInfo = info;
    }

    public void setProvider(@Nonnull final IServiceProvider provider) {
        _provider = provider;
    }

    @CheckForNull
    public String getDatatype() {
        return _datatype;
    }
}
