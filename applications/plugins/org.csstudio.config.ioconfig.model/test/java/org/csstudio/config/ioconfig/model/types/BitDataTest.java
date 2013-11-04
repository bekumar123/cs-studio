package org.csstudio.config.ioconfig.model.types;

import static org.junit.Assert.assertThat;

import org.csstudio.config.ioconfig.model.pbmodel.DataType;
import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.common.base.Optional;

public class BitDataTest {

    @Test
    public void testLowByte() {
        BitData bitData = new BitData(Integer.parseInt("0203", 16));
        assertThat(bitData.getLowByte().getValue(), Is.is(3));
    }

    @Test
    public void testHighByte() {
        BitData bitData = new BitData(Integer.parseInt("FF03", 16));
        assertThat(bitData.getHighByte().getValue(), Is.is(255));
    }

    @Test
    public void testOneByteData() {
        BitData bitData = new BitData(230);
        assertThat(bitData.isHighestBitSet(DataType.INT8), Is.is(true));
        bitData = new BitData(128);
        assertThat(bitData.isHighestBitSet(DataType.INT8), Is.is(true));
        bitData = new BitData(127);
        assertThat(bitData.isHighestBitSet(DataType.INT8), Is.is(false));
        bitData = new BitData(512);
        assertThat(bitData.isHighestBitSet(DataType.INT16), Is.is(false));
        bitData = new BitData(Integer.parseInt("8000", 16));
        assertThat(bitData.isHighestBitSet(DataType.INT16), Is.is(true));
    }

    @Test
    public void testFromHex() {
        Optional<HighByte> highByte = Optional.of(new HighByte(3));
        LowByte lowByte = new LowByte(3);
        BitData bitData = new BitData(highByte, lowByte);
        assertThat(bitData.getIntValue(), Is.is(771));
    }

}
