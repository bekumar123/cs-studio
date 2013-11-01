package org.csstudio.config.ioconfig.model.types;

public class BitRange {

    private final int minBit;

    private final int maxBit;

    public BitRange(int minBit, int maxBit) {

        if (minBit < 0) {
            throw new IllegalArgumentException("minBit must not be negativ but was " + Integer.toString(minBit));
        }

        if (maxBit > 15) {
            throw new IllegalArgumentException("maxBit must not be greater 15 but was "
                    + Integer.toString(maxBit));
        }

        if (minBit > maxBit) {
            throw new IllegalArgumentException("minBit must not be greater than maxBit.");
        }

        this.minBit = minBit;
        this.maxBit = maxBit;
    }

    public boolean needsTwoBytes() {
        return maxBit > 7 && maxBit < 16;
    }

    public Integer getMinBit() {
        return minBit;
    }

    public Integer getMaxBit() {
        return maxBit;
    }

    public static BitRange createFromMaxValue(Integer maxValue) {      
        int highestOnBitValue =  Integer.highestOneBit(maxValue);
        int counter = 0;
        while ((highestOnBitValue & (1 << counter)) == 0 ) {
            counter++;
        }
        return new BitRange(0, counter);
    }
}
