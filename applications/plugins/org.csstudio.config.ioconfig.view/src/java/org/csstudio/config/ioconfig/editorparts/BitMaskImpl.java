package org.csstudio.config.ioconfig.editorparts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/*
 * @inheritDoc
 */
public class BitMaskImpl implements BitMask {

    public int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final Integer lowByte) {
        List<Integer> values = new ArrayList<Integer>();
        values.add(lowByte);
        return getValueFromBitMask(dataMinBit, dataMaxBit, values);
    }

    public int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final Integer highByte,
            @Nonnull final Integer lowByte) {
        List<Integer> values = new ArrayList<Integer>();
        values.add(lowByte);
        values.add(highByte);
        return getValueFromBitMask(dataMinBit, dataMaxBit, values);
    }

    public int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final List<Integer> values) {

        // check arguments

        if (dataMinBit < 0) {
            throw new IllegalArgumentException("dataMinBit must not be negativ but was " + Integer.toString(dataMinBit));
        }

        if (dataMaxBit > 15) {
            throw new IllegalArgumentException("dataMaxBit must not be greater 15 but was "
                    + Integer.toString(dataMaxBit));
        }

        if (values.get(0) > 255) {
            throw new IllegalArgumentException("a single low byte value must not be > 255 but was "
                    + values.get(0).toString());
        }

        if (values.size() > 2) {
            throw new IllegalArgumentException("max expected size is 2 but found " + values.size());
        }

        // implementation

        if (values.size() == 0) {
            return 0;
        }

        int highByte = 0;
        if (values.size() > 1) {
            highByte = values.get(1);
        }

        if (highByte > 255) {
            throw new IllegalArgumentException("a single high byte value must not be > 255 but was " + highByte);
        }

        // e.g. val = 15
        int val = highByte * 256 + values.get(0);
        int sign = (int)Math.signum(val);
        val = Math.abs(val);
                
        if (dataMaxBit < dataMinBit) {
            throw new IllegalStateException("minBit must not be greater than maxBit");
        }

        // mask for minBit = 1 and maxBit = 3 is gives a mask of 14
        final int mask = (int) (Math.pow(2, dataMaxBit + 1) - Math.pow(2, dataMinBit));

        // 15 & 14 = 1111 & 1110 = 1110
        val = (val & mask);

        // 1110 >> 1 = 0111 => 7
        val = val >> dataMinBit;

        if (sign == -1) {
            return val * -1;
        } else {
            return val;
        }
        
    }

}
