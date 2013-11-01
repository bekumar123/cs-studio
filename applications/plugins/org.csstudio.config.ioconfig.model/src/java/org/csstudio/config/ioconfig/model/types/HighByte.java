package org.csstudio.config.ioconfig.model.types;

public class HighByte implements IByteType {

    private final Integer value;
    private final ByteEncoding byteEncoding;

    public HighByte(final Integer value, final ByteEncoding byteEncoding) {
        this.value = value;
        this.byteEncoding = byteEncoding;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public boolean isTwoComplement() {
        return byteEncoding == ByteEncoding.TWO_COMPLEMENT;
    }

}
