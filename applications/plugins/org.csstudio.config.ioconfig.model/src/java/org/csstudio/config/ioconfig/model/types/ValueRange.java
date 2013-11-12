package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;

public class ValueRange {

    private final Integer minValue;

    private final Integer maxValue;

    public ValueRange(Integer minValue, Integer maxValue) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public static Optional<ValueRange> createFromTextDescription(String rangeDescription) {
        Optional<Integer> hyphenPos = getHyphenPos(rangeDescription);
        if (hyphenPos.isPresent()) {
            String min = rangeDescription.substring(0, hyphenPos.get()).trim();
            String max = rangeDescription.substring(hyphenPos.get() + 1).trim();
            return Optional.of(new ValueRange(Integer.parseInt(min), Integer.parseInt(max)));
        } else {
            return Optional.absent();
        }
    }

    private static Optional<Integer> getHyphenPos(String dataTypeParameter) {
        for (int i = 1; i < dataTypeParameter.length() - 1; i++) {
            char charBefore = dataTypeParameter.charAt(i - 1);
            char currentChar = dataTypeParameter.charAt(i);
            if ((currentChar == '-') && (Character.isDigit(charBefore))) {
                return Optional.of(i);
            }
        }
        return Optional.absent();
    }
}
