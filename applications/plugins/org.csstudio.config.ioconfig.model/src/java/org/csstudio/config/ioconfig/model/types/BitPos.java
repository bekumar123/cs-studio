package org.csstudio.config.ioconfig.model.types;

public class BitPos {
    
    private final int bitPos;

    public BitPos(int bitPos) {

        if (bitPos < 0) {
            throw new IllegalArgumentException("minBit must not be negativ but was " + Integer.toString(bitPos));
        }

        if (bitPos > 15) {
            throw new IllegalArgumentException("maxBit must not be greater 15 but was " + Integer.toString(bitPos));
        }

        this.bitPos = bitPos;
    }

    public int getBitPos() {
        return bitPos;
    }
        
}
