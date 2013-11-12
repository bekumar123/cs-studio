package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;

public class UnsignedByteValue implements IByteType {

    private final int value;

    public UnsignedByteValue(final Integer value) {
        Preconditions.checkArgument(value >= 0, "value must not be negativ");
        Preconditions.checkArgument(value <= 255, "value must not be greater than 255");
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

}
