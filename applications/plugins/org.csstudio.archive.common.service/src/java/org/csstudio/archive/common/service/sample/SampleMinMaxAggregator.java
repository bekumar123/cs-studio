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
package org.csstudio.archive.common.service.sample;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.calc.CumulativeAverageCache;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.time.TimeInstant;

import com.google.common.collect.Ordering;

/**
 * Sample aggregator for average, min, max values for {@link java.lang.Double} type values.
 * Caches the sum of the accumulated values and stores information about the timestamps of the
 * last accumulated sample.
 *
 * @author bknerr
 * @since 25.11.2010
 */
public class SampleMinMaxAggregator {

    private final CumulativeAverageCache _avg = new CumulativeAverageCache();

    private Double _minVal;
    private Double _maxVal;
    private Double _lastAvgBeforeReset;
    private TimeInstant _lastSampleTimeStamp;
    private TimeInstant _resetTimeStamp;

    private EpicsAlarmStatus _status;
    private EpicsAlarmSeverity _severity;

    private int _count;

    /**
     * Constructor.
     */
    public SampleMinMaxAggregator(@Nonnull final Double firstVal,
                                  @Nonnull final Double firstMin,
                                  @Nonnull final Double firstMax,
                                  @Nonnull final TimeInstant timestamp) {
        _avg.accumulate(firstVal);

        _minVal = firstMin;
        _maxVal = firstMax;
        _lastAvgBeforeReset = null;

        _lastSampleTimeStamp = timestamp;
        _resetTimeStamp = _lastSampleTimeStamp;

        _count = 0;
    }
    /**
     * Constructor.
     */
    public SampleMinMaxAggregator(@Nonnull final Double firstVal,
                                  @Nonnull final TimeInstant timestamp) {
        this(firstVal, firstVal, firstVal, timestamp);
    }
    /**
     * Constructor.
     */
    public SampleMinMaxAggregator() {
        // EMPTY
    }

    public void aggregate(@Nonnull final Double newVal,
                          final EpicsAlarmStatus status,
                          final EpicsAlarmSeverity severity,
                          @Nonnull final TimeInstant timestamp) {
        aggregate(newVal, newVal, newVal, status, severity, timestamp, 1);
    }

    public synchronized void aggregate(@Nonnull final Double newVal,
                                       @Nonnull final Double min,
                                       @Nonnull final Double max,
                                       final EpicsAlarmStatus status,
                                       final EpicsAlarmSeverity severity,
                                       @Nonnull final TimeInstant timestamp,
                                       final int count) {
        _avg.accumulate(newVal);
        _minVal = Ordering.natural().nullsLast().min(newVal, min, max, _minVal);
        _maxVal = Ordering.natural().nullsFirst().max(newVal, min, max, _maxVal);

        // first simple approach: archive last value of status and severity
        _status = status;
        _severity = severity;

        _lastSampleTimeStamp = timestamp;
        _count += count;
    }

    /**
     * Resets the aggregator. <br/>
     * Caches the timestamp of the last aggregated sample.
     * Caches the last average value.
     * Sets the minimum and maximum values to <code>null</code>.
     * Clears the current average value cache.
     */
    public synchronized void reset() {
        _resetTimeStamp = _lastSampleTimeStamp;
        _lastSampleTimeStamp = null;
        _lastAvgBeforeReset = _avg.getValue();
        _minVal = null;
        _maxVal = null;
        _status = null;
        _severity = null;
        _avg.clear();
        _count = 0;
    }
    @CheckForNull
    public synchronized Double getAvg() {
        return _avg.getValue();
    }
    @CheckForNull
    public synchronized Double getMin() {
        return _minVal;
    }
    @CheckForNull
    public synchronized Double getMax() {
        return _maxVal;
    }
    public EpicsAlarmStatus getStatus() {
        return _status;
    }
    public EpicsAlarmSeverity getSeverity() {
        return _severity;
    }
    @CheckForNull
    public synchronized Double getAverageBeforeReset() {
        return _lastAvgBeforeReset;
    }
    @CheckForNull
    public synchronized TimeInstant getSampleTimestamp() {
        return _lastSampleTimeStamp;
    }
    @CheckForNull
    public synchronized TimeInstant getResetTimestamp() {
        return _resetTimeStamp;
    }

    public int getCount() {
        return _count;
    }
}
