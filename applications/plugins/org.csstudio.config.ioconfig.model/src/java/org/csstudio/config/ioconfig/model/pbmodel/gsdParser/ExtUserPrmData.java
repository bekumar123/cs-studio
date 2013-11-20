/*
            String valuePart = dataTypeParameterParts[2];
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: ExtUserPrmData.java,v 1.3 2010/08/20 13:33:08 hrickens Exp $
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.types.BitRange;
import org.csstudio.config.ioconfig.model.types.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.3 $
 * @since 21.07.2008
 */
public class ExtUserPrmData {

    private static final Logger LOG = LoggerFactory.getLogger(ExtUserPrmData.class);

    /**
     * The Parent GSD Slave Model.
     */
    private final ParsedGsdFileModel _gsdFileModel;

    /**
     * The ref index of this ext user prm data.
     */
    private final Integer _index;
    /**
     * The Name/Desc of this ext user prm data.
     */
    private String _text;
    /**
     * The dataType of this ext user prm data as plain text.<br>
     * (e.G. Bit(1) or BitArea(4-7))
     */
    private String _dataType;
    /**
     * The default value.
     */
    private int _default;
    /**
     * The lowest bit to manipulate.
     */
    private int _minBit;
    /**
     * The highest bit to manipulate.
     */
    private int _maxBit;
    /**
     * The ref index for the Prm Text.
     */
    private Integer _prmTextRef;
    private SortedSet<Integer> _values;

    private boolean _range;

    /**
     * @param gsdSlaveModel
     *            The Parent GSD Slave Model.
     * @param index
     *            The ref index of this ext user prm data.
     * @param text
     *            The Name/Desc of this ext user prm data.
     */
    public ExtUserPrmData(@Nonnull final ParsedGsdFileModel gsdFileModel, @Nonnull final Integer index,
            @Nonnull final String text) {
        _gsdFileModel = gsdFileModel;
        _index = index;
        setText(text);
    }

    /**
     * @param dataTypeParameter
     */
    public void buildDataTypeParameter(@Nonnull final String dataTypeParameter) {
        final String[] dataTypeParameterParts = dataTypeParameter.split(";")[0].split("\\s+");
        if (dataTypeParameterParts.length == 3) {
            String valuePart = dataTypeParameterParts[2];
            Optional<ValueRange> valueRange = ValueRange.createFromTextDescription(valuePart);
            setDataType(dataTypeParameterParts[0], valueRange);
            setDefault(dataTypeParameterParts[1]);
            if (valueRange.isPresent()) {
                String min = valueRange.get().getMinValue().toString();
                String max = valueRange.get().getMaxValue().toString();
                setValueRange(min, max);
            } else if (valuePart.contains(",")) {
                setValues(valuePart.split(","));
            } else {
                LOG.error("Unkown DataType Values: {}", dataTypeParameter);
            }
        } else {
            LOG.error("Unkown DataType!");
        }
    }

    /**
     * The dataType of this ext user prm data as plain text.<br>
     * (e.G. Bit(1), BitArea(4-7), Unsigned)
     * 
     * @return the plain text dataType.
     */
    @Nonnull
    public final String getDataType() {
        if (_dataType == null) {
            _dataType = "";
        }
        return _dataType;
    }

    public boolean isSigned() {
        return getDataType().toUpperCase().startsWith("SIGNED");
    }

    /**
     * 
     * @return the default value.
     */
    public final int getDefault() {
        return _default;
    }

    /**
     * 
     * @return The ref index of this ext user prm data.
     */
    @Nonnull
    public final Integer getIndex() {
        return _index;
    }

    /**
     * 
     * @return The highest bit to manipulate.
     */
    public final int getMaxBit() {
        return _maxBit;
    }

    /**
     * @return maximum Value;
     */
    public final int getMaxValue() {
        Integer max = 0;
        if (_values != null) {
            max = _values.last();
        }
        return max;

    }

    /**
     * 
     * @return The lowest bit to manipulate.
     */
    public final int getMinBit() {
        return _minBit;
    }

    /**
     * @return minimum Value;
     */
    public final int getMinValue() {
        Integer min = 0;
        if (_values != null) {
            min = _values.first();
        }
        return min;
    }

    /**
     * 
     * @return The Parameter Text Map.
     */
    @CheckForNull
    public final PrmText getPrmText() {
        PrmText prmText = null;
        final Integer prmTextRef = getPrmTextRef();
        if (prmTextRef != null) {
            prmText = _gsdFileModel.getPrmTextMap().get(prmTextRef);
        }
        return prmText;
    }

    /**
     * @return The Parameter Text Reference.
     */
    @Nonnull
    public final Integer getPrmTextRef() {
        return _prmTextRef;
    }

    /**
     * 
     * @return The Name/Desc of this ext user prm data.
     */
    @Nonnull
    public final String getText() {
        return _text;
    }

    public boolean isValuesRanged() {
        return _range;
    }

    /**
     * 
     * @param dataType
     *            set the plain text DataType.
     * @param valueRange
     */
    public final void setDataType(@Nonnull final String dataType, Optional<ValueRange> valueRange) {
        String[] split = dataType.split("[\\(\\)]");
        if (split.length > 1) {
            if (split[1].contains("-")) {
                split = split[1].split("-");
                if (split.length == 2) {
                    setMinBit(split[0]);
                    setMaxBit(split[1]);
                }
            } else {
                setMinBit(split[1]);
                setMaxBit(split[1]);
            }

        } else if (split[0].endsWith("8")) {
            if (valueRange.isPresent()) {
                BitRange bitRange;
                if (valueRange.get().getMinValue() >= 0) {
                    bitRange = BitRange.createFromMaxValue(valueRange.get().getMaxValue());
                } else {
                    bitRange = BitRange.createFromMaxValue(valueRange.get().getMinValue());
                }
                setMinBit(bitRange.getMinBitAsString());
                setMaxBit(bitRange.getMaxBitAsString());
            } else {
                setMinBit("0");
                setMaxBit("7");
            }
        } else if (split[0].endsWith("16")) {
            if (valueRange.isPresent()) {
                BitRange bitRange;
                if (valueRange.get().getMinValue() >= 0) {
                    bitRange = BitRange.createFromMaxValue(valueRange.get().getMaxValue());
                } else {
                    bitRange = BitRange.createFromMaxValue(valueRange.get().getMinValue());
                }
                setMinBit(bitRange.getMinBitAsString());
                setMaxBit(bitRange.getMaxBitAsString());
            } else {
                setMinBit("0");
                setMaxBit("15");
            }
        } else {
            LOG.error("Unkown DataType: {}", dataType);
        }

        _dataType = dataType;
    }

    /**
     * Set a numeric int value, given as string.
     * 
     * @param def
     *            set the default value.
     */
    public final void setDefault(@Nonnull final String def) {
        try {
            _default = Integer.parseInt(def);
        } catch (final NumberFormatException nfe) {
            _default = 0;
        }
    }

    /**
     * 
     * @param maxBit
     *            Set the highest bit to manipulate.
     */
    public final void setMaxBit(@Nonnull final String maxBit) {
        try {
            _maxBit = Integer.parseInt(maxBit);
        } catch (final NumberFormatException nfe) {
            _maxBit = 0;
        }
    }

    /**
     * 
     * @param minBit
     *            Set the lowest bit to manipulate.
     */
    public final void setMinBit(@Nonnull final String minBit) {
        try {
            _minBit = Integer.parseInt(minBit);
        } catch (final NumberFormatException nfe) {
            _minBit = 0;
        }
    }

    /**
     * 
     * @param integer
     *            Set the Parameter Text Reference.
     */
    public final void setPrmTextRef(@Nonnull final Integer prmTextRef) {
        _prmTextRef = prmTextRef;
    }

    /**
     * 
     * @param text
     *            Set the Name/Desc of this ext user prm data.
     */
    public final void setText(@Nonnull final String text) {
        if (text != null && !text.isEmpty()) {
            _text = text.split(";")[0].trim();
        } else {
            _text = "";
        }
    }

    /**
     * @param minValue
     *            Set the minimum Value.
     * @param maxValue
     *            Set the maximum Value.
     */
    public final void setValueRange(@Nonnull final String minValue, @Nonnull final String maxValue) {
        _values = new TreeSet<Integer>();
        _range = true;
        try {
            _values.add(GsdFileParser.gsdValue2Int(minValue));
        } catch (final NumberFormatException nfe) {
            _values.add(0);
        }
        try {
            _values.add(GsdFileParser.gsdValue2Int(maxValue));
        } catch (final NumberFormatException nfe) {
            _values.add(0);
        }
    }

    public void setValues(@Nullable final String[] values) {
        if (values != null) {
            _range = false;
            _values = new TreeSet<Integer>();
            for (final String value : values) {
                _values.add(GsdFileParser.gsdValue2Int(value));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String toString() {
        return getIndex() + " : " + getText() + "(" + getDataType() + ")";
    }

}
