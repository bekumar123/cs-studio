package org.csstudio.config.ioconfig.model.types;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class ModuleNumberTest {

    @Test
    public void shouldNotBeVersioned() {
        ModuleNumber moduleNumber = ModuleNumber.moduleNumber(1).get();
        assertThat(moduleNumber.isVersioned(), is(false));
    }

    @Test
    public void shouldBeVersioned() {
        ModuleNumber moduleNumber = ModuleNumber.moduleNumber(1).get();
        ModuleNumber newModuleNumber = moduleNumber.createNextVersion();
        assertThat(newModuleNumber.isVersioned(), is(true));
    }

    @Test
    public void shouldBeGreater() {
        ModuleNumber moduleNumber = ModuleNumber.moduleNumber(1).get();
        ModuleNumber newModuleNumber = moduleNumber.createNextVersion();
        assertThat(newModuleNumber.isVersioned(), is(true));
           assertTrue(newModuleNumber.getValue() > moduleNumber.getValue());
        ModuleNumber newNewModuleNumber = newModuleNumber.createNextVersion();
        assertTrue(newNewModuleNumber.getValue() > newModuleNumber.getValue());
        assertThat(newNewModuleNumber.getValue(), is(2000001));   
    }

}
