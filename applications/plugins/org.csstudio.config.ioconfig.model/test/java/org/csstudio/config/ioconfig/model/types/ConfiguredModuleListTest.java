package org.csstudio.config.ioconfig.model.types;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ConfiguredModuleListTest {

    private ConfiguredModuleList configuredModuleList;
    
    @Before
    public void setUp() {
        List<ModuleId> moduleIdList = new ArrayList<ModuleId>();
        moduleIdList.add(new ModuleId(new BigDecimal(12)));
        moduleIdList.add(new ModuleId(new BigDecimal(13)));
        moduleIdList.add(new ModuleId(new BigDecimal(14)));
        configuredModuleList = new ConfiguredModuleList(moduleIdList);
    }
    
    @Test
    public void shouldConfirmThatIdIsInList() {
        assertThat(configuredModuleList.isPresent(new ModuleId(new BigDecimal(12))), is(true));
        assertThat(configuredModuleList.isPresent(new ModuleId(new BigDecimal(13))), is(true));
        assertThat(configuredModuleList.isPresent(new ModuleId(new BigDecimal(14))), is(true));
    }

    public void shouldConfirmThatIdIsNotInList() {
        assertThat(configuredModuleList.isPresent(new ModuleId(new BigDecimal(1))), is(false));                
        assertThat(configuredModuleList.isPresent(new ModuleId(new BigDecimal(11))), is(false));                
    }

}
