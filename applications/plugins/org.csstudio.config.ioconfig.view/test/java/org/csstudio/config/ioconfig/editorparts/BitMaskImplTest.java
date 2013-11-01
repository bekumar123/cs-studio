package org.csstudio.config.ioconfig.editorparts;

import static org.junit.Assert.assertThat;

import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.ByteEncoding;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;
import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.common.base.Optional;

public class BitMaskImplTest {

    private BitMask bitMask = new BitMaskImpl();
    private Optional<HighByte> noHighByte = Optional.absent();

    @Test
    public void testBitMaskForBit0And0() {
        int result = bitMask.getValueFromBitMask(new BitRange(0, 0), noHighByte, getLowByte());
        assertThat(result, Is.is(1));
    }

    @Test
    public void testBitMaskForBit1And2() {
        int result = bitMask.getValueFromBitMask(new BitRange(1, 2), noHighByte, getLowByte());
        assertThat(result, Is.is(3));
    }

    @Test
    public void testBitMaskForBit2And3() {
        int result = bitMask.getValueFromBitMask(new BitRange(2, 3), noHighByte, getLowByte());
        assertThat(result, Is.is(3));
    }

    @Test
    public void testBitMaskForBit1And3() {
        int result = bitMask.getValueFromBitMask(new BitRange(1, 3), noHighByte, getLowByte());
        assertThat(result, Is.is(7));
    }

    @Test
    public void testBitMaskForBit1AndTwoByte() {
        int result = bitMask.getValueFromBitMask(new BitRange(8, 8), getHighByte(), getLowByte());
        assertThat(result, Is.is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBitMaskForMinBitGreaterMaxBit() {
        bitMask.getValueFromBitMask(new BitRange(1, 0), noHighByte, getLowByte());
    }

    private LowByte getLowByte() {
        return new LowByte(15, ByteEncoding.DEFAULT);
    }

    private Optional<HighByte> getHighByte() {
        return Optional.of(new HighByte(1, ByteEncoding.DEFAULT));
    }

}
