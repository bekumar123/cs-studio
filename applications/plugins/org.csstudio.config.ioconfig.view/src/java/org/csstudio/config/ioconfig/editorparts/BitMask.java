package org.csstudio.config.ioconfig.editorparts;

import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;

import com.google.common.base.Optional;

/*
 * Takes bits minBit upto maxBit, normalize them to bit pos 0 and return the value 
 */
public interface BitMask {
    int getValueFromBitMask(BitRange bitRange, Optional<HighByte> highByte, LowByte lowByte);
}
