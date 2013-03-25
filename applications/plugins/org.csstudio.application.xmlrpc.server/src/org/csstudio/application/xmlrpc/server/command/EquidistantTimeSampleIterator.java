
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

package org.csstudio.application.xmlrpc.server.command;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.csstudio.archive.common.reader.AbstractValueIterator;
import org.csstudio.archive.common.reader.EquidistantTimeBinsIterator;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.IArchiveChannel;
import org.csstudio.archive.common.service.sample.IArchiveMinMaxSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.csstudio.domain.desy.typesupport.BaseTypeConversionSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.epics.pvmanager.util.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;

/**
 * Generates an iterator for channels that are
 *
 * @author bknerr
 * @since Feb 24, 2011
 */
@SuppressWarnings("rawtypes")
public class EquidistantTimeSampleIterator extends AbstractValueIterator {

    /**
     * Result container for method
     * {@link EquidistantTimeBinsIterator#findWindowOfFirstSample(TimeInstant, TimeInstant, ReadableDuration)}.
     *
     * @author bknerr
     * @since Mar 16, 2011
     */
    private static final class SampleAndWindow {
        private final IArchiveSample _sample;
        private final int _window;
        /**
         * Constructor.
         */
        public SampleAndWindow(IArchiveSample sample, final int window) {
            _sample = sample;
            _window = window;
        }
        public int getWindow() {
            return _window;
        }
        public IArchiveSample getSample() {
            return _sample;
        }
    }

    private final ReadableDuration _windowLength;

    private final int _numOfWindows;
    private int _currentWindow = 1;

    private IArchiveSample _firstSample;
    private IArchiveSample _lastSampleOfLastWindow;
    private final Display display;
    private SampleMinMaxAggregator _agg;
    private final boolean _noSamples;

    /**
     * Constructor.
     * @throws ArchiveServiceException
     * @throws OsgiServiceUnavailableException
     */
    public EquidistantTimeSampleIterator(IArchiveReaderFacade reader,
                                         Collection<IArchiveSample> samples,
                                         String channelName,
                                         TimeInstant start,
                                         TimeInstant end,
                                         int timeBins) throws OsgiServiceUnavailableException,
                                                              ArchiveServiceException {
        super(samples, channelName, start, end);
        _numOfWindows = timeBins;

        if (_numOfWindows <= 0) {
            throw new IllegalArgumentException("Number of time bins less equal zero.");
        }

        display = retrieveMetaDataForChannel(reader, channelName);

        _windowLength = calculateWindowLength(start, end, _numOfWindows);

        _lastSampleOfLastWindow = reader.readLastSampleBefore(channelName, start);

        final SampleAndWindow saw = findFirstSampleAndItsWindow(start,
                                                                _windowLength,
                                                                getIterator());
        _firstSample = saw.getSample();
        final int firstSampleWindow = saw.getWindow();

        if (_lastSampleOfLastWindow != null) {
            _currentWindow = 1;
        } else {
            _currentWindow = firstSampleWindow;
        }

        _noSamples = _lastSampleOfLastWindow == null && _firstSample == null;
    }

    private void initAggregator(IArchiveSample sample) throws TypeSupportException {
        if (_agg == null) {
            _agg = new SampleMinMaxAggregator();
        } else {
            _agg.reset();
        }
        if (sample != null) {
            aggregateSample(_agg, sample);
        }
    }

    private SampleAndWindow findFirstSampleAndItsWindow(TimeInstant startTime,
                                                        ReadableDuration windowLength,
                                                        Iterator<IArchiveSample> samplesIter) {

        if (samplesIter != null && samplesIter.hasNext()) {
            @SuppressWarnings("unchecked")
            final IArchiveSample<Serializable, ISystemVariable<Serializable>> firstSampleInWindow = samplesIter.next();
            final int window = findWindowOfFirstSample(firstSampleInWindow.getSystemVariable().getTimestamp(),
                                                       startTime,
                                                       windowLength);
            return new SampleAndWindow(firstSampleInWindow, window);
        }
        return new SampleAndWindow(null, 1);
    }

    private Duration calculateWindowLength(TimeInstant start,
                                           TimeInstant end,
                                           final int bins) {
        return new Duration((end.getMillis() - start.getMillis()) / bins);
    }

    private
    Display retrieveMetaDataForChannel(IArchiveReaderFacade reader,
                                                String channelName) throws ArchiveServiceException, OsgiServiceUnavailableException {
        final IArchiveChannel ch = reader.getChannelByName(channelName);
        if (ch == null) {
            throw new ArchiveServiceException("Channel retrieval failed for channel '" + channelName + "'!", null);
        }
        final Limits<?> l = reader.readDisplayLimits(channelName);
        if (l != null) {
            return ValueFactory.newDisplay(new Double(((Double)l.getLow()).doubleValue()),  new Double(0.0),  new Double(0.0), "", NumberFormats.toStringFormat(),  new Double(0.0),
                                           new Double(0.0), new Double(((Double)l.getHigh()).doubleValue()),  new Double(0.0),  new Double(((Double)l.getLow()).doubleValue()));
        }
        return null;
    }


    private int findWindowOfFirstSample(TimeInstant sampleTime,
                                        TimeInstant startTime,
                                        ReadableDuration windowLength) {
        int i = 1;
        TimeInstant nextWindowEnd =
            TimeInstantBuilder.fromMillis(startTime.getMillis()).plusMillis(windowLength.getMillis());
        while (sampleTime.isAfter(nextWindowEnd)) {
            nextWindowEnd = nextWindowEnd.plusMillis(windowLength.getMillis());
            i++;
        }
        return i;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (!_noSamples && _currentWindow <= _numOfWindows) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VType next() throws Exception {
        if (_noSamples || _currentWindow > _numOfWindows) {
            throw new NoSuchElementException();
        }

        initAggregator(_lastSampleOfLastWindow);

        final TimeInstant curWindowEnd = calculateCurrentWindowEndTime(getStart(), _currentWindow, _windowLength);
        if (_firstSample != null && hasTimestampBeforeWindowEnd(_firstSample, curWindowEnd)) {
            _firstSample =
                aggregateSamplesUntilWindowEnd(_firstSample, curWindowEnd, getIterator(), _agg);
        }

        final VType iVal = createMinMaxDoubleValue(curWindowEnd, display, _agg);

        _currentWindow++;
        return iVal;
    }

    private VType createMinMaxDoubleValue(TimeInstant curWindowEnd,
                                           Display display,
                                           SampleMinMaxAggregator agg) throws Exception {
        final Double avg = agg.getAvg();
        final Double min = agg.getMin();
        final Double max = agg.getMax();

        if (avg != null && min != null && max != null) {
            //TODO (jhatje): implement vType
            return ValueFactory.newVStatistics(avg, 0, min, max, 1, ValueFactory.alarmNone(), ValueFactory.newTime(Timestamp.of(curWindowEnd.getSeconds(), (int)curWindowEnd.getFractalMillisInNanos())),display);
           /* return ValueFactory.createMinMaxDoubleValue(BaseTypeConversionSupport.toTimestamp(curWindowEnd),
                                                        new Severity("OK"), null, metaData, IValue.Quality.Interpolated,
                                                        new double[]{avg.doubleValue()},
                                                        min.doubleValue(),
                                                        max.doubleValue());*/
        }
        throw new Exception("Creation of MinMaxDoubleValue failed. " + SampleMinMaxAggregator.class.getName() + " returned null values.");
    }

    private boolean hasTimestampBeforeWindowEnd(IArchiveSample firstSample,
                                              TimeInstant curWindowEnd) {
        return curWindowEnd.isAfter(firstSample.getSystemVariable().getTimestamp());
    }

    private TimeInstant calculateCurrentWindowEndTime(TimeInstant startTime,
                                                      final int currentWindow,
                                                      ReadableDuration windowLength) {
        return startTime.plusMillis(currentWindow*windowLength.getMillis());
    }

    /**
     * Uses iterator to find samples before current window end and aggregates all those found.
     * Returns either the first sample provided by the iterator that lies outside the current window
     * or <code>null</code> if there isn't any further sample.
     */
    private IArchiveSample
    aggregateSamplesUntilWindowEnd(IArchiveSample initSample,
                                   TimeInstant windowEnd,
                                   Iterator<IArchiveSample> iter,
                                   SampleMinMaxAggregator aggregator) throws TypeSupportException {

        _lastSampleOfLastWindow = initSample; // store the last sample of the last window

        aggregateSample(aggregator, initSample);

        IArchiveSample nextSample;
        while (iter.hasNext()) {
            nextSample = iter.next();

            if (belongsToCurrentWindow(nextSample, windowEnd)) {
                aggregateSample(aggregator, nextSample);
                _lastSampleOfLastWindow = nextSample;
            } else {
                return nextSample; // is the first 'real' sample in one of the next windows
            }
        }
        return null;
    }

    private boolean belongsToCurrentWindow(IArchiveSample sample,
                                           TimeInstant windowEnd) {
        return !sample.getSystemVariable().getTimestamp().isAfter(windowEnd);
    }

    private void aggregateSample(SampleMinMaxAggregator aggregator,
                                 IArchiveSample sample) throws TypeSupportException {

        final TimeInstant curSampleTime = sample.getSystemVariable().getTimestamp();

        final Double value = BaseTypeConversionSupport.toDouble(sample.getValue());
        Double minimum = null;
        Double maximum = null;
        if (sample instanceof IArchiveMinMaxSample) {
            minimum = BaseTypeConversionSupport.toDouble(((IArchiveMinMaxSample) sample).getMinimum());
            maximum = BaseTypeConversionSupport.toDouble(((IArchiveMinMaxSample) sample).getMaximum());
        } else {
            minimum = value;
            maximum = value;
        }

        aggregator.aggregate(value,
                             minimum == null ? value : BaseTypeConversionSupport.toDouble(minimum),
                             maximum == null ? value : BaseTypeConversionSupport.toDouble(maximum),
                             curSampleTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        // Nothing to do
    }
}
