package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;

public class BitRange {

    private final BitPos minBit;

    private final BitPos maxBit;

    public BitRange(final BitPos minBit, final BitPos maxBit) {

        if (minBit.getBitPos() > maxBit.getBitPos()) {
            throw new IllegalArgumentException("minBit must not be greater than maxBit.");
        }

        this.minBit = minBit;
        this.maxBit = maxBit;
    }

    public boolean needsTwoBytes() {
        return maxBit.getBitPos() > 7;
    }

    public BitPos getMinBit() {
        return minBit;
    }

    public BitPos getMaxBit() {
        return maxBit;
    }

    public String getMinBitAsString() {
        return String.valueOf(minBit.getBitPos());
    }

    public String getMaxBitAsString() {
        return String.valueOf(maxBit.getBitPos());
    }

    public String toString() {
        return getMinBitAsString() + "-" + getMaxBitAsString();
    }

    public static BitRange createFromMaxValue(Integer maxValue) {
        Preconditions.checkNotNull(maxValue, "maxValue must not be null");
        BitData bitData = new BitData(Math.abs(maxValue));
        return new BitRange(new BitPos(0), bitData.calculateHighestBit());
    }
}
