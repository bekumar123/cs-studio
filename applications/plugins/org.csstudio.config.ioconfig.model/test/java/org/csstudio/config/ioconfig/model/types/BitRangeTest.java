package org.csstudio.config.ioconfig.model.types;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

public class BitRangeTest {

    @Test
    public void testCreateBitRange() {
        BitRange bitRange = BitRange.createFromMaxValue(8);
        assertThat(bitRange.getMinBit(), Is.is(0));
        assertThat(bitRange.getMaxBit(), Is.is(3));
        
        bitRange = BitRange.createFromMaxValue(15);
        assertThat(bitRange.getMinBit(), Is.is(0));
        assertThat(bitRange.getMaxBit(), Is.is(3));

        bitRange = BitRange.createFromMaxValue(128);
        assertThat(bitRange.getMinBit(), Is.is(0));
        assertThat(bitRange.getMaxBit(), Is.is(7));
    }

}
