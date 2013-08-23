package org.csstudio.dct.nameresolution.file.types;

import org.csstudio.dct.nameresolution.file.parser.Constant;
import org.csstudio.dct.nameresolution.file.service.SpsParseException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public enum SpsType {

    //@formatter:off
    BOOL("BOOL", 1),
    BYTE("BYTE", 1),
    CHAR("CHAR", 1),
    WORD("WORD", 2),
    INT("INT", 2),
    S5TIME("S5TIME", 2),
    DWORD("DWORD", 4), 
    TIME("TIME", 4), 
    DINT("DINT", 4), 
    REAL("REAL", 4),
    TIME_OF_DAY("TIME_OF_DAY", 4),
    STRING(SpsType.STRING_TYPE),
    ARRAY(SpsType.ARRAY_TYPE),
    DS33("'DS33'", 5);
    //@formatter:on

    private static final String STRING_TYPE = "STRING";
    private static final String ARRAY_TYPE = "ARRAY";

    private String typeName;
    private int size;

    private SpsType(String spsType, int size) {
        this.typeName = spsType.toUpperCase().trim();
        this.size = size;
    }

    private SpsType(String spsType) {
        this.typeName = spsType.toUpperCase().trim();
        this.size = 0;
    }

    public static SpsType getSpsType(String typeName) throws SpsParseException {
        if (typeName.startsWith(STRING_TYPE)) {
            SpsType type = SpsType.STRING;
            type.setSize(SpsType.calculateStringSize(typeName));
            return type;
        } else if (typeName.startsWith(ARRAY_TYPE)) {
            SpsType type = SpsType.ARRAY;
            type.setSize(SpsType.calculateArraySize(typeName));
            return type;
        }
        for (SpsType value : SpsType.values()) {
            if (value.typeName.equals(typeName.trim())) {
                return value;
            }
        }
        throw new SpsParseException("Unknown type " + typeName);
    }

    public String getTypeName() {
        return typeName;
    }

    public int getSize() {
        return size;
    }

    private void setSize(int size) {
        Preconditions.checkArgument(size > 0);
        this.size = size;
    }

    /**
     * Calculate new address.
     */
    public SpsAddress calculateAddress(SpsAddress spsAddress, Optional<SpsType> lastType) {
        Preconditions.checkNotNull(spsAddress, "spsAddress must not be null");
        Preconditions.checkNotNull(spsAddress, "lastType must not be null");
        Integer currentAddress = spsAddress.getAddress();
        if (!lastType.isPresent()) {
            // First entry, therefore lastType is not present.
            if (this == SpsType.BOOL) {
                return new SpsAddress(currentAddress, 0);
            } else {
                return new SpsAddress(currentAddress);
            }
        }
        if ((this == SpsType.BOOL) && (lastType.get() == SpsType.BOOL)) {
            return handleBoolType(spsAddress, lastType);
        } else {
            currentAddress = currentAddress + lastType.get().size;
            if (mustStartOnEvenAddress(lastType) && ((currentAddress % 2) != 0)) {
                currentAddress = currentAddress + 1;
            }
            if (this == SpsType.BOOL) {
                return new SpsAddress(currentAddress, 0);
            } else {
                return new SpsAddress(currentAddress);
            }
        }
    }

    private boolean mustStartOnEvenAddress(Optional<SpsType> lastType) {
        if (size > 1) {
            return true;
        } else {
            if (lastType.isPresent() && (lastType.get() == SpsType.DS33)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Bool Type needs special handling, since a Bool address contains the
     * address and the bit position.
     */
    private SpsAddress handleBoolType(SpsAddress spsAddress, Optional<SpsType> lastType) {
        Integer currentAddress = spsAddress.getAddress();
        Optional<Integer> currentBitPos = spsAddress.getBitPos();
        int nextBitPos;
        if (currentBitPos.isPresent()) {
            nextBitPos = currentBitPos.get() + 1;
        } else {
            throw new IllegalStateException("BOOL without bit position.");
        }
        //@formatter:off
        if (lastType.isPresent() && 
           (lastType.get() == SpsType.BOOL) &&
           (nextBitPos > Constant.MAX_BIT)) {
               return new SpsAddress(currentAddress + 1, 0);
               //@formatter:on
        }
        return new SpsAddress(currentAddress, nextBitPos);
    }

    private static int calculateStringSize(String typeName) throws SpsParseException {
        if (!typeName.contains("[")) {
            throw new SpsParseException("Invalid String format: " + typeName);
        }
        if (!typeName.contains("]")) {
            throw new SpsParseException("Invalid String format: " + typeName);
        }
        String theDigits = CharMatcher.DIGIT.retainFrom(typeName);
        return Integer.valueOf(theDigits) + 2;
    }

    private static int calculateArraySize(String typeName) throws SpsParseException {
        if (!typeName.contains("[0..")) {
            throw new SpsParseException("Invalid Array format: " + typeName);
        }
        if (!typeName.contains("]")) {
            throw new SpsParseException("Invalid Array format: " + typeName);
        }
        String theDigits = CharMatcher.DIGIT.retainFrom(typeName);
        return Integer.valueOf(theDigits) + 1;
    }

}
