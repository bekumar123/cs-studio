package org.csstudio.config.ioconfig.model.types;

import org.csstudio.config.ioconfig.model.pbmodel.DataType;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class BitData {

    private final Integer value;

    public BitData(Integer value) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(value >= 0, "value must not be negativ");
        this.value = value;
    }

    public BitData(Optional<HighByte> highByte, LowByte lowByte) {
        Preconditions.checkNotNull(highByte, "highByte must not be null");
        Preconditions.checkNotNull(lowByte, "lowByte must not be null");
        if (highByte.isPresent()) {
            value = highByte.get().getValue() * 256 + lowByte.getValue();
        } else {
            value = lowByte.getValue();
        }
    }

    public UnsignedByteValue getLowByte() {
        return new UnsignedByteValue(value % 256);
    }

    public UnsignedByteValue getHighByte() {
        return new UnsignedByteValue((value - getLowByte().getValue()) / 256);
    }
    
    public BitPos calculateHighestBit() {
        int highestOnBitValue = Integer.highestOneBit(value);
        int counter = 0;
        while ((highestOnBitValue & (1 << counter)) == 0) {
            counter++;
        }
        return new BitPos(counter);
    }

    public boolean isHighestBitSet(DataType dataType) {
        Preconditions.checkNotNull(dataType, "dataType must not be null");
        int maskValue = 1 <<  (8) * (dataType.getByteSize()) - 1; 
        return (value & maskValue) == maskValue;
    }

    public BitData shiftLeftToBit(BitPos minBit) {
        Preconditions.checkNotNull(minBit, "minBit must not be null");
        if (minBit.getBitPos() == 0) {
            return new BitData(value);            
        }
        int newValue = value << minBit.getBitPos();
        return new BitData(newValue);
    }

    // get bit range and move it to bit position 0
    public BitData normalize(BitRange bitRange) {

        Preconditions.checkNotNull(bitRange, "bitRange must not be null");

        final int mask = (int) (Math.pow(2, bitRange.getMaxBit().getBitPos() + 1) - Math.pow(2, bitRange.getMinBit().getBitPos()));

        // 15 & 14 = 1111 & 1110 = 1110
        int val = value & mask;

        // 1110 >> 1 = 0111 => 7
        val = val >> bitRange.getMinBit().getBitPos();

        return new BitData(val);

    }

    public int getIntValue() {
        return value;
    }

    public int asTwoComplement() {
        if (value.intValue() == 128) {
            return -128;
        } else {
            return ((value ^ 255) + 1) * -1;
        }
    }

}
