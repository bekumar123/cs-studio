package org.csstudio.config.ioconfig.editorparts;

import org.csstudio.config.ioconfig.model.types.BitData;
import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.HighByte;
import org.csstudio.config.ioconfig.model.types.LowByte;

import com.google.common.base.Optional;

/*
 * @inheritDoc
 */
public class BitMaskImpl implements BitMask {

    public int getValueFromBitMask(BitRange bitRange, Optional<HighByte> highByte, LowByte lowByte) {
        BitData bitData = new BitData(highByte, lowByte);
        bitData = bitData.normalize(bitRange);        
        return bitData.getIntValue();        
    }

}
