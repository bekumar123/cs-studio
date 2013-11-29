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
package org.csstudio.archive.common.service.mysqlimpl.sample;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.archive.common.service.ArchiveConnectionException;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.controlsystem.IArchiveControlSystem;
import org.csstudio.archive.common.service.mysqlimpl.batch.BatchQueueHandlerSupport;
import org.csstudio.archive.common.service.mysqlimpl.channel.IArchiveChannelDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.AbstractArchiveDao;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveConnectionHandler;
import org.csstudio.archive.common.service.mysqlimpl.dao.ArchiveDaoException;
import org.csstudio.archive.common.service.mysqlimpl.persistengine.PersistEngineDataManager;
import org.csstudio.archive.common.service.mysqlimpl.requesttypes.DesyArchiveRequestType;
import org.csstudio.archive.common.service.sample.ArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.common.statistic.ArchiveSampleBatchQueueCollector;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.system.SystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Archive sample dao implementation.
 *
 * @author bknerr
 * @since 11.11.2010
 */
public class ArchiveSampleDaoImpl extends AbstractArchiveDao implements IArchiveSampleDao {
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveSampleDaoImpl.class);

    public static final String TAB_SAMPLE = "sample";
    public static final String TAB_SAMPLE_M = "sample_m";
    public static final String TAB_SAMPLE_H = "sample_h";
    public static final String TAB_SAMPLE_BLOB = "sample_blob";

    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_CHANNEL_ID = "channel_id";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SERVERTY = "serverty";
    public static final String COLUMN_AVG = "avg_val";
    public static final String COLUMN_MIN = "min_val";
    public static final String COLUMN_MAX = "max_val";
    public static final String  COLUMN_COUNT="count";

    private static final String ARCH_TABLE_PLACEHOLDER = "<arch.table>";
    private static final String RETRIEVAL_FAILED = "Sample retrieval from archive failed.";

    private static final String SELECT_RAW_PREFIX = "SELECT * ";
    private final String _selectRawSamplesStmt = SELECT_RAW_PREFIX + "FROM " + getDatabaseName() + "."
                                                 + ARCH_TABLE_PLACEHOLDER + " WHERE " + COLUMN_CHANNEL_ID + "=? " + "AND "
                                                 + COLUMN_TIME + " BETWEEN ? AND ?";
    private final String _selectOptSamplesStmt = "SELECT "
                                                 + Joiner.on(",").join(COLUMN_CHANNEL_ID,
                                                                       COLUMN_TIME,
                                                                       COLUMN_AVG,
                                                                       COLUMN_MIN,
                                                                       COLUMN_MAX) + " " + "FROM " + getDatabaseName() + "."
                                                 + ARCH_TABLE_PLACEHOLDER + " WHERE " + COLUMN_CHANNEL_ID + "=? " + "AND "
                                                 + COLUMN_TIME + " BETWEEN ? AND ?";
    private final String _selectLatestSampleBeforeTimeStmt = SELECT_RAW_PREFIX + "FROM " + getDatabaseName() + "."
                                                             + TAB_SAMPLE + " WHERE " + COLUMN_CHANNEL_ID + "=? " + "AND "
                                                             + COLUMN_TIME + "<? ORDER BY " + COLUMN_TIME + " DESC LIMIT 1";
    private final String _selectSampleExistsForChannel = "SELECT * FROM " + getDatabaseName() + "." + ARCH_TABLE_PLACEHOLDER
                                                         + " WHERE " + COLUMN_CHANNEL_ID + "=? LIMIT 1";

    private final ConcurrentMap<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForMinutes = new MapMaker()
            .concurrencyLevel(2).weakKeys().makeMap();
    private final ConcurrentMap<ArchiveChannelId, SampleMinMaxAggregator> _reducedDataMapForHours = new MapMaker()
            .concurrencyLevel(2).weakKeys().makeMap();

    private final IArchiveChannelDao _channelDao;
    private static Map<Integer, String> _statusmap = new MapMaker().makeMap();
    private static Map<Integer, String> _servertymap = new MapMaker().makeMap();

    /**
     * Constructor.
     */
    @Inject
    public ArchiveSampleDaoImpl(@Nonnull final ArchiveConnectionHandler handler,
                                @Nonnull final PersistEngineDataManager persister,
                                @Nonnull final IArchiveChannelDao channelDao) {
        super(handler, persister);
        _channelDao = channelDao;
        ArchiveTypeConversionSupport.install();
        EpicsSystemVariableSupport.install();
        BatchQueueHandlerSupport.installHandlerIfNotExists(new ArchiveSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new CollectionDataSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new MinuteReducedDataSampleBatchQueueHandler(getDatabaseName()));
        BatchQueueHandlerSupport.installHandlerIfNotExists(new HourReducedDataSampleBatchQueueHandler(getDatabaseName()));
       }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V extends Serializable, T extends ISystemVariable<V>> int createSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples) throws ArchiveDaoException {
        final int size = 0;
        final Collection<IArchiveSample<V, T>> s = new ArrayList<IArchiveSample<V, T>>();
        for (final IArchiveSample<V, T> ss : samples) {
            final ArchiveSample<V, T> mySample = (ArchiveSample<V, T>) ss;
            if (ss instanceof ArchiveSample) {
                mySample.setStatusIndex(((EpicsAlarm) ((ArchiveSample) ss).getAlarm()).getStatus().ordinal() * 100
                                        + getStatusId(((EpicsAlarm) ((ArchiveSample) ss).getAlarm()).getStatus().toString()));
                mySample.setServertyIndex(((EpicsAlarm) ((ArchiveSample) ss).getAlarm()).getSeverity().ordinal()
                                          * 100
                                          + getServertyId(((EpicsAlarm) ((ArchiveSample) ss).getAlarm()).getSeverity()
                                                  .toString()));
                s.add(mySample);
            }
        }
        //   try {
        try {
            ArchiveSampleBatchQueueCollector.getInstance().getArchiveSampleBatchQueueApplication().setValue(getEngineMgr().submitToBatch(s));
        } catch (final TypeSupportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<MinuteReducedDataSample> minuteSamples = Lists.newLinkedList();
        try {
            minuteSamples = generatePerMinuteSamples(s, _reducedDataMapForMinutes);
            if (minuteSamples.size() > 0) {
                ArchiveSampleBatchQueueCollector.getInstance().getArchiveSample_mBatchQueueApplication().setValue(getEngineMgr().submitToBatch(minuteSamples));
            }
        } catch (final TypeSupportException e1) {
            e1.printStackTrace();
        }

        try {
            final List<HourReducedDataSample> hourSamples = generatePerHourSamples(minuteSamples, _reducedDataMapForHours);
            if (hourSamples.size() > 0) {
                ArchiveSampleBatchQueueCollector.getInstance().getArchiveSample_hBatchQueueApplication().setValue(getEngineMgr().submitToBatch(hourSamples));
           }
        } catch (final TypeSupportException e) {
             e.printStackTrace();
        }

        return size;
    }

    @Nonnull
    private List<HourReducedDataSample> generatePerHourSamples(@Nonnull final Collection<? extends AbstractReducedDataSample> samples,
                                                               @Nonnull final ConcurrentMap<ArchiveChannelId, SampleMinMaxAggregator> aggregatorMap) throws ArchiveDaoException {

        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<HourReducedDataSample> hourSamples = Lists.newLinkedList();

        for (final AbstractReducedDataSample sample : samples) {

            final ArchiveChannelId channelId = sample.getChannelId();
            final Double newValue = sample.getAvg();
            final Double minValue = sample.getMin();
            final Double maxValue = sample.getMax();
            final TimeInstant time = sample.getTimestamp();
            final int severty = sample.getSeverty();
            final int status = sample.getStatus();
            final SampleMinMaxAggregator agg =
                                               retrieveAndInitializeAggregator(channelId,
                                                                               aggregatorMap,
                                                                               newValue,
                                                                               minValue,
                                                                               maxValue,
                                                                               time);
            agg.setCount(sample.getCount());
            processHourSampleOnTimeCondition(hourSamples, channelId, newValue, time, agg, status, severty);
        }
        return hourSamples;
    }

    private void processHourSampleOnTimeCondition(@Nonnull final List<HourReducedDataSample> hourSamples,
                                                  @Nonnull final ArchiveChannelId channelId,
                                                  @Nonnull final Double newValue,
                                                  @Nonnull final TimeInstant time,
                                                  @Nonnull final SampleMinMaxAggregator agg,
                                                  @Nonnull final int status,
                                                  @Nonnull final int severty) {
        if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Hours.ONE.toStandardDuration())) {
            final Double avg = agg.getAvg();
            final Double min = agg.getMin();
            final Double max = agg.getMax();
            if (avg != null && min != null && max != null) {
                hourSamples.add(new HourReducedDataSample(channelId, time, avg, min, max, status, severty, agg.getCount()));
            }
            agg.reset();
        }
    }

    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>> List<MinuteReducedDataSample> generatePerMinuteSamples(@Nonnull final Collection<IArchiveSample<V, T>> samples,
                                                                                                                          @Nonnull final ConcurrentMap<ArchiveChannelId, SampleMinMaxAggregator> aggregatorMap) throws TypeSupportException,
                                                                                                                                                                                                               ArchiveDaoException {
        if (samples.isEmpty()) {
            return Collections.emptyList();
        }
        final List<MinuteReducedDataSample> minuteSamples = Lists.newLinkedList();

        for (final IArchiveSample<V, T> sample : samples) {
            final T sysVar = sample.getSystemVariable();
            final V data = sysVar.getData();

            if (ArchiveTypeConversionSupport.isDataTypeOptimizable(data.getClass())) {
                final Double newValue = BaseTypeConversionSupport.createDoubleFromValueOrNull(sysVar);
                if (newValue == null || newValue.isInfinite() || newValue.isNaN()) {
                    LOG.warn("Channel {} have a error value {} ", sysVar.getName(), sysVar.getData().toString());
                    continue;
                }
                final EpicsAlarm alarm = (EpicsAlarm) ((ArchiveSample) sample).getAlarm();

                int severty = 0;
                ;
                int status = 0;
                ;
                if (alarm != null) {
                    severty = ((ArchiveSample<V, T>) sample).getServertyIndex();
                    status = ((ArchiveSample<V, T>) sample).getStatusIndex();
                }

                final ArchiveChannelId channelId = sample.getChannelId();
                final Double minValue = newValue;
                final Double maxValue = newValue;
                final TimeInstant time = sample.getSystemVariable().getTimestamp();

                final SampleMinMaxAggregator agg =
                                                   retrieveAndInitializeAggregator(channelId,
                                                                                   aggregatorMap,
                                                                                   newValue,
                                                                                   minValue,
                                                                                   maxValue,
                                                                                   time);
                agg.setCount(1);
                processMinuteSampleOnTimeCondition(minuteSamples, newValue, channelId, time, agg, status, severty);

            }
        }
        return minuteSamples;

    }

    private void processMinuteSampleOnTimeCondition(@Nonnull final List<MinuteReducedDataSample> minuteSamples,
                                                    @Nonnull final Double newValue,
                                                    @Nonnull final ArchiveChannelId channelId,
                                                    @Nonnull final TimeInstant time,
                                                    @Nonnull final SampleMinMaxAggregator agg,
                                                    @Nonnull final int status,
                                                    @Nonnull final int severty) {
        if (isReducedDataWriteDueAndHasChanged(newValue, agg, time, Minutes.ONE.toStandardDuration())) {
            final Double avg = agg.getAvg();
            final Double min = agg.getMin();
            final Double max = agg.getMax();
            if (avg != null && min != null && max != null) {
               minuteSamples.add(new MinuteReducedDataSample(channelId, time, avg, min, max, status, severty,agg.getCount()));
            }
            agg.reset();
        }
    }

    @Nonnull
    private SampleMinMaxAggregator retrieveAndInitializeAggregator(@Nonnull final ArchiveChannelId channelId,
                                                                   @Nonnull final ConcurrentMap<ArchiveChannelId, SampleMinMaxAggregator> aggMap,
                                                                   @Nonnull final Double value,
                                                                   @Nonnull final Double min,
                                                                   @Nonnull final Double max,
                                                                   @Nonnull final TimeInstant time) throws ArchiveDaoException {

        SampleMinMaxAggregator aggregator = aggMap.get(channelId);
        if (aggregator == null) {
            aggregator = new SampleMinMaxAggregator();
            aggMap.put(channelId, aggregator);
            initAggregatorToLastKnownSample(channelId, aggregator);
        }
        aggregator.aggregate(value, min, max, time);
        return aggregator;
    }

    private void initAggregatorToLastKnownSample(@Nonnull final ArchiveChannelId channelId,
                                                 @Nonnull final SampleMinMaxAggregator aggregator) throws ArchiveDaoException {
        final Collection<IArchiveChannel> channels = _channelDao.retrieveChannelsByIds(Sets.newHashSet(channelId));
        if (channels.isEmpty()) {
            throw new ArchiveDaoException("Init sample aggregator failed. Channel with id " + channelId.intValue()
                                          + " does not exist.", null);
        }
        final IArchiveSample<Serializable, ISystemVariable<Serializable>> sample =
                                                                                   retrieveLatestSample(channels.iterator()
                                                                                           .next());
        if (sample != null) {
            final Double lastWrittenValue =
                                            BaseTypeConversionSupport
                                                    .createDoubleFromValueOrNull(sample.getSystemVariable());
            if (lastWrittenValue != null) {
                final TimeInstant lastWriteTime = sample.getSystemVariable().getTimestamp();
                aggregator.aggregate(lastWrittenValue, lastWriteTime);
            }
        }
    }

    private boolean isReducedDataWriteDueAndHasChanged(@Nonnull final Double newVal,
                                                       @Nonnull final SampleMinMaxAggregator agg,
                                                       @Nonnull final TimeInstant timestamp,
                                                       @Nonnull final Duration duration) {

        final TimeInstant lastWriteTime = agg.getResetTimestamp();
        if (lastWriteTime == null) {
            return true;
        }
        final TimeInstant dueTime = lastWriteTime.plusMillis(duration.getMillis());
        if (timestamp.isBefore(dueTime)) {
            return false; // not yet due, don't write
        }
        final Double lastWrittenValue = agg.getAverageBeforeReset();
        if (lastWrittenValue != null && lastWrittenValue.compareTo(newVal) == 0) {
            return false; // hasn't changed much TODO (bknerr) : consider a sort of sensitivity here
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>> Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                                                                                   @Nonnull final ArchiveChannelId channelId,
                                                                                                                   @Nonnull final TimeInstant start,
                                                                                                                   @Nonnull final TimeInstant end) throws ArchiveDaoException {
        final Collection<IArchiveChannel> channels = _channelDao.retrieveChannelsByIds(Sets.newHashSet(channelId));
        if (!channels.isEmpty()) {
            return retrieveSamples(type, channels.iterator().next(), start, end);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public <V extends Serializable, T extends ISystemVariable<V>> Collection<IArchiveSample<V, T>> retrieveSamples(@Nullable final DesyArchiveRequestType type,
                                                                                                                   @Nonnull final IArchiveChannel channel,
                                                                                                                   @Nonnull final TimeInstant start,
                                                                                                                   @Nonnull final TimeInstant end) throws ArchiveDaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        Collection<IArchiveSample<V, T>> samples = Collections.emptyList();
        try {
            DesyArchiveRequestType reqType =
                                             type != null ? // if null = determine automatically
                                                         type : SampleRequestTypeUtil.determineRequestType(channel
                                                                 .getDataType(), start, end);
            conn = createConnection();
        //    do {
                stmt = createReadSamplesStatement(conn, channel, start, end, reqType);
                result = stmt.executeQuery();
                if (result.next()) {
                   samples = createRetrievedSamplesContainer(channel,reqType,result);

                } else if (type == null) { // type == null means use automatic lookup
                    reqType = reqType.getNextLowerOrderRequestType();
                }
       //     } while (type == null && reqType != null); // no other request type of lower order

        } catch (final Exception ex) {
            handleExceptions(RETRIEVAL_FAILED + "\n" + "ArchiveSampleDaoImpl.retrieveSamples() " + channel.getName(), ex);
        } finally {
            closeSqlResources(result, stmt, conn, "Samples retrieval for " + channel.getName());
        }
        return samples;
    }

    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>> Collection<IArchiveSample<V, T>> createRetrievedSamplesContainer(@Nonnull final IArchiveChannel channel,
                                                                                                                                    @Nonnull final DesyArchiveRequestType reqType,
                                                                                                                                    @CheckForNull final ResultSet result) throws SQLException,
                                                                                                                                                                         ArchiveDaoException,
                                                                                                                                                                         TypeSupportException {
        final List<IArchiveSample<V, T>> samples = Lists.newArrayList();
        while (!result.isClosed()&& result != null && !result.isAfterLast()) {
            final IArchiveSample<V, T> sample = createSampleFromQueryResult(reqType, channel, result);
            samples.add(sample);
            if(!result.isClosed()) {
                result.next();
            }
        }
        return samples;
    }

    @Nonnull
    private PreparedStatement createReadSamplesStatement(@Nonnull final Connection conn,
                                                         @Nonnull final IArchiveChannel channel,
                                                         @Nonnull final TimeInstant s,
                                                         @Nonnull final TimeInstant e,
                                                         @Nonnull final DesyArchiveRequestType reqType) throws SQLException,
                                                                                                       ArchiveDaoException,
                                                                                                       TypeSupportException {
        final String dataType = channel.getDataType();
        if (dataType == null) {
            throw new ArchiveDaoException("Data type of channel " + channel.getName() + " is unknown.", null);
        }
        final PreparedStatement stmt = dispatchRequestTypeToStatement(conn, reqType, dataType);
        stmt.setInt(1, channel.getId().intValue());
        stmt.setLong(2, s.getNanos());
        stmt.setLong(3, e.getNanos());
        return stmt;
    }

    @Nonnull
    private PreparedStatement dispatchRequestTypeToStatement(@Nonnull final Connection conn,
                                                             @Nonnull final DesyArchiveRequestType type,
                                                             @Nonnull final String dataType) throws SQLException,
                                                                                            TypeSupportException {

        PreparedStatement stmt = null;
        switch (type) {
            case RAW:
                if (ArchiveTypeConversionSupport.isDataTypeSerializableCollection(dataType)) {
                    stmt =
                           conn.prepareStatement(_selectRawSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_BLOB));
                } else {
                    stmt = conn.prepareStatement(_selectRawSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE));
                }
                break;
            case AVG_PER_MINUTE:
                stmt = conn.prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_M));
                break;
            case AVG_PER_HOUR:
                stmt = conn.prepareStatement(_selectOptSamplesStmt.replaceFirst(ARCH_TABLE_PLACEHOLDER, TAB_SAMPLE_H));
                break;
            default:
        }
        return stmt;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private <V extends Serializable, T extends ISystemVariable<V>> IArchiveSample<V, T> createSampleFromQueryResult(@Nonnull final DesyArchiveRequestType type,
                                                                                                                    @Nonnull final IArchiveChannel channel,
                                                                                                                    @Nonnull final ResultSet result) throws SQLException,
                                                                                                                                                    ArchiveDaoException,
                                                                                                                                                    TypeSupportException {
        final Class<V> typeClass = createDataTypeClass(channel);

        V value = null;
        V min = null;
        V max = null;
        switch (type) {
            case RAW: { // (..., value)
                if (ArchiveTypeConversionSupport.isDataTypeSerializableCollection(typeClass)) {
                    value = ArchiveTypeConversionSupport.fromByteArray(result.getBytes(COLUMN_VALUE));
                } else {
                    value = ArchiveTypeConversionSupport.fromArchiveString(typeClass, result.getString(COLUMN_VALUE));
                }
                break;
            }
            case AVG_PER_MINUTE:
            case AVG_PER_HOUR: { // (..., avg_val, min_val, max_val)
                value = ArchiveTypeConversionSupport.fromDouble(typeClass, result.getDouble(COLUMN_AVG));
                min = ArchiveTypeConversionSupport.fromDouble(typeClass, result.getDouble(COLUMN_MIN));
                max = ArchiveTypeConversionSupport.fromDouble(typeClass, result.getDouble(COLUMN_MAX));
                break;
            }
            default:
                throw new ArchiveDaoException("Archive request type unknown. Sample could not be created from query", null);
        }
        final long time = result.getLong(COLUMN_TIME);
        final TimeInstant timeInstant = TimeInstantBuilder.fromNanos(time);
        final IArchiveControlSystem cs = channel.getControlSystem();
        if (value == null) {
            return null;
        }
        final ISystemVariable<V> sysVar =
                                          SystemVariableSupport.create(channel.getName(),
                                                                       value,
                                                                       ControlSystem.valueOf(cs.getName(), cs.getType()),
                                                                       timeInstant);
        if (min == null || max == null) {
            return new ArchiveSample<V, T>(channel.getId(), (T) sysVar, null);
        }
        return new ArchiveMinMaxSample<V, T>(channel.getId(), (T) sysVar, null, min, max);
    }

    @SuppressWarnings("unchecked")
    @CheckForNull
    private <V> Class<V> createDataTypeClass(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException,
                                                                                    TypeSupportException {
        final String datatype = channel.getDataType();
        if (datatype == null) {
            throw new ArchiveDaoException("The datatype of channel " + channel.getName() + " is unknown!", null);
        }
        return (Class<V>) ArchiveTypeConversionSupport.createTypeClassFromArchiveString(datatype);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V extends Serializable, T extends ISystemVariable<V>> IArchiveSample<V, T> retrieveLatestSample(@Nonnull final IArchiveChannel channel) throws ArchiveDaoException {
        final TimeInstant latestTimestamp = channel.getLatestTimestamp();
        if (latestTimestamp == null) {
            return null;
        }
        final Collection<IArchiveSample<V, T>> samples =
                                                         retrieveSamples(DesyArchiveRequestType.RAW,
                                                                         channel.getId(),
                                                                         latestTimestamp,
                                                                         latestTimestamp.plusNanosPerSecond(1L));
        return samples.isEmpty() ? null : samples.iterator().next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public <V extends Serializable, T extends ISystemVariable<V>> IArchiveSample<V, T> retrieveLatestSampleBeforeTime(@Nonnull final IArchiveChannel channel,
                                                                                                                      @Nonnull final TimeInstant time) throws ArchiveDaoException {
        final TimeInstant latestTimestamp = channel.getLatestTimestamp();
        if (latestTimestamp == null) {
            return null;
        }
        if (!time.isBefore(latestTimestamp)) {
            return retrieveLatestSample(channel);
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(_selectLatestSampleBeforeTimeStmt);
            stmt.setInt(1, channel.getId().intValue());
            stmt.setLong(2, time.getNanos());
            result = stmt.executeQuery();
            if (result.next()) {
                return createSampleFromQueryResult(DesyArchiveRequestType.RAW, channel, result);
            }
        } catch (final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeSqlResources(result, stmt, conn, _selectLatestSampleBeforeTimeStmt);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doesSampleExistForChannelId(@Nonnull final ArchiveChannelId id) throws ArchiveDaoException {
        if (checkForSamplesInTable(id, TAB_SAMPLE)) {
            return true;
        }
        return checkForSamplesInTable(id, TAB_SAMPLE_BLOB);
    }

    private boolean checkForSamplesInTable(@Nonnull final ArchiveChannelId id, @Nonnull final String table) throws ArchiveDaoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        final String stmtStr = _selectSampleExistsForChannel.replace(ARCH_TABLE_PLACEHOLDER, table);
        try {
            conn = createConnection();
            stmt = conn.prepareStatement(stmtStr);
            stmt.setInt(1, id.intValue());
            rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (final Exception e) {
            handleExceptions(RETRIEVAL_FAILED, e);
        } finally {
            closeSqlResources(rs, stmt, conn, stmtStr);
        }
        return false;
    }

    private int getStatusId(@Nonnull final String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        final String _selectRawStatusStmt = "Select * FROM " + getDatabaseName() + ".epics_status";
        Integer id = null;
        if (!_statusmap.isEmpty() || _statusmap.containsValue(name)) {
            for (final Entry<Integer, String> entry : _statusmap.entrySet()) {
                if (name.equals(entry.getValue())) {
                    id = entry.getKey();
                }
            }

        } else {
            try {
                conn = createConnection();
                stmt = conn.prepareStatement(_selectRawStatusStmt);
                result = stmt.executeQuery();
                while (result != null && !result.isAfterLast()) {
                    if (result.next()) {
                        final Integer i = result.getInt("id");
                        final String s = result.getString("name");
                        _statusmap.put(i, s);
                    }

                }
                for (final Entry<Integer, String> entry : _statusmap.entrySet()) {
                    if (name.equals(entry.getValue())) {
                        id = entry.getKey();
                    }
                }
            } catch (final ArchiveConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally{
                closeSqlResources(result, stmt, conn, _selectRawStatusStmt);
            }
        }
        return id;

    }

    private int getServertyId(@Nonnull final String name) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        final String _selectRawServertyStmt = "Select * FROM " + getDatabaseName() + ".epics_serverty";
        Integer id = null;
        if (!_servertymap.isEmpty() || _servertymap.containsValue(name)) {
            for (final Entry<Integer, String> entry : _servertymap.entrySet()) {
                if (name.equals(entry.getValue())) {
                    id = entry.getKey();
                }
            }

        } else {
            try {
                conn = createConnection();
                stmt = conn.prepareStatement(_selectRawServertyStmt);
                result = stmt.executeQuery();
                while (result != null && !result.isAfterLast()) {
                    if (result.next()) {
                        final Integer i = result.getInt("id");
                        final String s = result.getString("name");
                        _servertymap.put(i, s);
                    }

                }
                for (final Entry<Integer, String> entry : _servertymap.entrySet()) {
                    if (name.equals(entry.getValue())) {
                        id = entry.getKey();
                    }
                }
            } catch (final ArchiveConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (final SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                closeSqlResources(result, stmt, conn, _selectRawServertyStmt);
            }
        }
        return id;

    }

}
