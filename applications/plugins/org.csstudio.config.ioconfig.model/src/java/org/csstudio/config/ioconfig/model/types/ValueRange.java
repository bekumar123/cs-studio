package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ValueRange {

    private final Integer minValue;

    private final Integer maxValue;

    public ValueRange(Integer minValue, Integer maxValue) {
        Preconditions.checkArgument(minValue <= maxValue, "minValue must not be greater than maxValue");
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    /*
     * Convert a string like 1-190 or -20-127 to a ValueRange object.
     */
    public static Optional<ValueRange> createFromTextDescription(final String rangeDescription) {
        
        Preconditions.checkNotNull(rangeDescription, "rangeDescription must not be null");
        
        Optional<Integer> hyphenPos = ValueRange.getHyphenPos(rangeDescription);
        if (hyphenPos.isPresent()) {
            String min = rangeDescription.substring(0, hyphenPos.get()).trim();
            String max = rangeDescription.substring(hyphenPos.get() + 1).trim();
            return Optional.of(new ValueRange(Integer.parseInt(min), Integer.parseInt(max)));
        } else {
            return Optional.absent();
        }
    }

    private static Optional<Integer> getHyphenPos(final String rangeDescription) {
        for (int i = 1; i < rangeDescription.length() - 1; i++) {
            char charBefore = rangeDescription.charAt(i - 1);
            char currentChar = rangeDescription.charAt(i);
            if ((currentChar == '-') && (Character.isDigit(charBefore))) {
                return Optional.of(i);
            }
        }
        return Optional.absent();
    }
}
