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
package org.csstudio.archive.common.service.sample;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmStatus;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Test;

/**
 * Test for {@link SampleMinMaxAggregator}.
 *
 * @author bknerr
 * @since 24.10.2011
 */
public class SampleMinMaxAggregatorUnitTest {

    @Test
    public void testDoubleMin() {
        final Double min = Double.MIN_VALUE;
        testOneValue(min);
    }

    @Test
    public void testDoubleMax() {
        final Double max = Double.MAX_VALUE;
        testOneValue(max);
    }

    private void testOneValue(@Nonnull final Double min) {
        final SampleMinMaxAggregator cache = new SampleMinMaxAggregator(min, TimeInstantBuilder.fromNow());

        Assert.assertEquals(min, cache.getAvg());
        Assert.assertEquals(min, cache.getMin());
        Assert.assertEquals(min, cache.getMax());

        cache.reset();

        cache.aggregate(min, EpicsAlarmStatus.HIHI, EpicsAlarmSeverity.MAJOR, TimeInstantBuilder.fromNow());

        Assert.assertEquals(min, cache.getAvg());
        Assert.assertEquals(min, cache.getMin());
        Assert.assertEquals(min, cache.getMax());
        Assert.assertEquals(EpicsAlarmStatus.HIHI, cache.getStatus());
        Assert.assertEquals(EpicsAlarmSeverity.MAJOR, cache.getSeverity());

        cache.aggregate(min, EpicsAlarmStatus.HIGH, EpicsAlarmSeverity.MINOR, TimeInstantBuilder.fromNow());

        Assert.assertEquals(min, cache.getAvg());
        Assert.assertEquals(min, cache.getMin());
        Assert.assertEquals(min, cache.getMax());
        Assert.assertEquals(EpicsAlarmStatus.HIGH, cache.getStatus());
        Assert.assertEquals(EpicsAlarmSeverity.MINOR, cache.getSeverity());

    }

    @Test
    public void testManyDoubleMins() {
        final TimeInstant now = TimeInstantBuilder.fromNow();
        final SampleMinMaxAggregator cache = new SampleMinMaxAggregator(Double.MIN_VALUE, now);
        for (int i = 0; i < 1000; i++) {
            cache.aggregate(Double.MIN_VALUE, EpicsAlarmStatus.NO_ALARM, EpicsAlarmSeverity.NO_ALARM, now.plusMillis(i));
        }
        Assert.assertEquals(Double.MIN_VALUE, cache.getAvg());
        Assert.assertEquals(Double.MIN_VALUE, cache.getMin());
        Assert.assertEquals(Double.MIN_VALUE, cache.getMax());
        Assert.assertEquals(1000, cache.getCount());
    }

}
