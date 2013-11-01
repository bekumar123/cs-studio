package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;

public class BitData {

    private final Integer value;
    private boolean isTwoComplement;

    public BitData(Integer value) {
        this.value = value;
    }

    public BitData(Optional<HighByte> highByte, LowByte lowByte) {
        if (highByte.isPresent()) {
            if (highByte.get().isTwoComplement()) {
                throw new IllegalStateException("TwoComplement is not yet supported for 2 Byte");
            }
            value = highByte.get().getValue() * 256 + lowByte.getValue();
        } else {
            value = Math.abs(lowByte.getValue());
        }
        isTwoComplement = lowByte.isTwoComplement();
    }

    public Integer getLowByte() {
        return value % 256;
    }

    public Integer getHighByte() {
        return (value - getLowByte()) / 256;
    }

    public BitData shiftLeftToBit(int minBit) {
        int newValue = value << minBit;
        return new BitData(newValue);
    }

    public BitData normalize(BitRange bitRange) {
        
        final int mask = (int) (Math.pow(2, bitRange.getMaxBit() + 1) - Math.pow(2, bitRange.getMinBit()));

        // 15 & 14 = 1111 & 1110 = 1110
        int val = (value & mask);

        // 1110 >> 1 = 0111 => 7
        val = val >> bitRange.getMinBit();

        if (isTwoComplement) {
            return new BitData(val * -1);                        
        } else {
            return new BitData(val);            
        }
        
    }

    public int getIntValue() {
        return value;
    }
    
}
