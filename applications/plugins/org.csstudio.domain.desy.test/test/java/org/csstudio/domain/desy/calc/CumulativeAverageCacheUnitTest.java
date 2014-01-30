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
package org.csstudio.domain.desy.calc;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the cache.
 *
 * @author bknerr
 * @since 03.12.2010
 */
public class CumulativeAverageCacheUnitTest {

    @Test
    public void testAccumulate() {
        final CumulativeAverageCache cache =
            new CumulativeAverageCache();

        Assert.assertEquals(null, cache.readValue());

        for (int i = 0; i <= 10; i++) {
            cache.accumulate(Double.valueOf(i));
        }
        Assert.assertEquals(5.0, cache.readValue());
        Assert.assertEquals(11, cache.getNumberOfAccumulations());
        cache.clear();
        Assert.assertEquals(null, cache.readValue());
        Assert.assertEquals(0, cache.getNumberOfAccumulations());


        for (int i = -5; i <= 5; i++) {
            cache.accumulate(Double.valueOf(i));
        }
        Assert.assertEquals(0.0, cache.readValue());
        Assert.assertEquals(11, cache.getNumberOfAccumulations());
        cache.clear();

        Assert.assertEquals(null, cache.readValue());
        Assert.assertEquals(0, cache.getNumberOfAccumulations());
    }
}
