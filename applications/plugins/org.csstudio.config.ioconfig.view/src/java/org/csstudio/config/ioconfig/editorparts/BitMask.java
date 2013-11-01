package org.csstudio.config.ioconfig.editorparts;

import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;

import com.google.common.base.Optional;

/*
 * Takes bits datMinBit upto dataMaxBit, normalize them to bit pos 1 and return the value 
 */
public interface BitMask {
    int getValueFromBitMask(BitRange bitRange, Optional<HighByte> highByte, LowByte lowByte);
}
