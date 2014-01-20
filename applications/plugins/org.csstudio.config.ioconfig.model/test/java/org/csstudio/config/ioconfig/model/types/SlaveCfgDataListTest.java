package org.csstudio.config.ioconfig.model.types;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;

import org.csstudio.config.ioconfig.model.types.helper.TypesTestData;
import org.junit.Test;

public class SlaveCfgDataListTest {

    @Test
    public void shouldHaveInput() {
        SlaveCfgDataList slaveCfgDataList = new SlaveCfgDataList(TypesTestData.createIntegerListWithValues(16));
        assertThat(slaveCfgDataList.isInput(), is(true));
    }

    @Test
    public void shouldHaveOutput() {
        SlaveCfgDataList slaveCfgDataList = new SlaveCfgDataList(TypesTestData.createIntegerListWithValues(32));
        assertThat(slaveCfgDataList.isOutput(), is(true));
    }

    @Test
    public void shouldHaveInputs() {
        SlaveCfgDataList slaveCfgDataList = new SlaveCfgDataList(TypesTestData.createIntegerListWithValues(16, 17));
        assertThat(slaveCfgDataList.isInput(), is(true));
    }

    @Test
    public void shouldHaveNoInputs() {
        SlaveCfgDataList slaveCfgDataList = new SlaveCfgDataList(TypesTestData.createIntegerListWithValues(16, 12));
        assertThat(slaveCfgDataList.isInput(), is(true));
    }

    @Test
    public void shouldHaveNoOutputs() {
        SlaveCfgDataList slaveCfgDataList = new SlaveCfgDataList(TypesTestData.createIntegerListWithValues(32, 12));
        assertThat(slaveCfgDataList.isOutput(), is(true));
    }

}
