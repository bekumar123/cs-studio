package org.csstudio.config.ioconfig.model.types;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.common.base.Optional;

public class ValueRangeTest {

    @Test
    public void testCreateValueRange() {
        Optional<ValueRange> valueRange = ValueRange.createFromTextDescription("0-123");
        assertThat(valueRange.get().getMinValue(), Is.is(0));
        assertThat(valueRange.get().getMaxValue(), Is.is(123));

        valueRange = ValueRange.createFromTextDescription("-90-123");
        assertThat(valueRange.get().getMinValue(), Is.is(-90));
        assertThat(valueRange.get().getMaxValue(), Is.is(123));

        valueRange = ValueRange.createFromTextDescription("-90--40");
        assertThat(valueRange.get().getMinValue(), Is.is(-90));
        assertThat(valueRange.get().getMaxValue(), Is.is(-40));
    }
}
