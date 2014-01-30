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

import static org.junit.Assert.assertEquals;

import org.csstudio.archive.common.service.sample.SampleMinMaxAggregator;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link SampleMinMaxAggregator}.
 *
 * @author bknerr
 * @since 20.12.2010
 */
public class SampleAggregatorUnitTest {
    
    
    @Test
    public void initTest() {
        final TimeInstant ts = TimeInstantBuilder.fromNow();
        final SampleMinMaxAggregator agg = new SampleMinMaxAggregator(1.0, /*null,*/ ts);
        
        Assert.assertEquals(Double.valueOf(1.0), agg.getAvg());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMax());
        Assert.assertEquals(ts, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());
    }
    
    @Test
    public void aggregateTest() {
        final SampleMinMaxAggregator agg = new SampleMinMaxAggregator(1.0, /*null,*/ TimeInstantBuilder.fromNow());
        
        final TimeInstant ts = TimeInstantBuilder.fromNow();
        final EpicsAlarmStatus status = EpicsAlarmStatus.HIGH;
        final EpicsAlarmSeverity severity = EpicsAlarmSeverity.MINOR;
        
        agg.aggregate(2.0, status, severity, ts);
        
        Assert.assertEquals(Double.valueOf(1.5), agg.getAvg());
        Assert.assertEquals(Double.valueOf(1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(2.0), agg.getMax());
        Assert.assertEquals(status, agg.getStatus());
        Assert.assertEquals(severity, agg.getSeverity());
        Assert.assertEquals(ts, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());
        
        
        final TimeInstant ts3 = TimeInstantBuilder.fromNow();
        final EpicsAlarmStatus status3 = EpicsAlarmStatus.READ;
        final EpicsAlarmSeverity severity3 = EpicsAlarmSeverity.MAJOR;
        
        agg.aggregate(3.0, -1.0, 20.0, status3, severity3, ts3, 1);
        Assert.assertEquals(Double.valueOf(2.0), agg.getAvg());
        Assert.assertEquals(Double.valueOf(-1.0), agg.getMin());
        Assert.assertEquals(Double.valueOf(20.0), agg.getMax());
        Assert.assertEquals(status3, agg.getStatus());
        Assert.assertEquals(severity3, agg.getSeverity());
        Assert.assertEquals(ts3, agg.getSampleTimestamp());
        Assert.assertNull(agg.getAverageBeforeReset());
        
        agg.reset();
        Assert.assertNull(agg.getAvg());
        Assert.assertNull(agg.getMin());
        Assert.assertNull(agg.getMax());
        Assert.assertNull(agg.getSampleTimestamp());
        Assert.assertNull(agg.getStatus());
        Assert.assertNull(agg.getSeverity());
        Assert.assertEquals(Double.valueOf(2.0), agg.getAverageBeforeReset());
        Assert.assertEquals(ts3, agg.getResetTimestamp());
    }
    
    @Test
    public void aggregateAlarmTest() {
        final SampleMinMaxAggregator agg = new SampleMinMaxAggregator(1.0,
                                                                      TimeInstantBuilder.fromNow());
        
        EpicsAlarmStatus status = EpicsAlarmStatus.NO_ALARM;
        EpicsAlarmSeverity severity = EpicsAlarmSeverity.NO_ALARM;
        
        agg.aggregate(2.0, status, severity,
                      TimeInstantBuilder.fromNow());
        agg.aggregate(2.0, status, severity,
                      TimeInstantBuilder.fromNow());
        
        status = EpicsAlarmStatus.LOW;
        severity = EpicsAlarmSeverity.MINOR;
        
        agg.aggregate(2.0, status, severity,
                      TimeInstantBuilder.fromNow());
        agg.aggregate(2.0, status, severity,
                      TimeInstantBuilder.fromNow());
        
        assertEquals(EpicsAlarmStatus.LOW, agg.getStatus());
        assertEquals(EpicsAlarmSeverity.MINOR, agg.getSeverity());
    }
    
}
