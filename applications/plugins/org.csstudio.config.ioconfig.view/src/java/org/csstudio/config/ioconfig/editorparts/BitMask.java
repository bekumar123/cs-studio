package org.csstudio.config.ioconfig.editorparts;

import java.util.List;

import javax.annotation.Nonnull;

/*
 * Takes bits datMinBit upto dataMaxBit, normalize them to bit pos 1 and return the value 
 */
public interface BitMask {

    int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final Integer lowByte);

    int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final Integer highByte,
            @Nonnull final Integer lowByte);

    int getValueFromBitMask(final int dataMinBit, int dataMaxBit, @Nonnull final List<Integer> values);
}
