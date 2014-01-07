package org.csstudio.config.ioconfig.model.types;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.common.base.Optional;

public class ModuleLabelTest {

    @Test
    public void labelShouldContainVersionInfo() {
       ModuleLabel ml = new ModuleLabel(ModuleNumber.moduleNumber(12).get(), new ModuleName("Test"), Optional.of(new VersionTag("Tag")));
       assertThat(ml.buildLabel(), is("12 : Test [Tag]"));
    }

    @Test
    public void labelShouldNotContainVersionInfo() {
        ModuleLabel ml = new ModuleLabel(ModuleNumber.moduleNumber(12).get(), new ModuleName("Test"), Optional.<VersionTag>absent());
        assertThat(ml.buildLabel(), is("12 : Test"));        
    }

    @Test
    public void labelShouldContainVersionInfoButNotModuleNumber() {
       ModuleLabel ml = new ModuleLabel(ModuleNumber.moduleNumber(12).get(), new ModuleName("Test"), Optional.of(new VersionTag("Tag")));
       assertThat(ml.buildLabelWithoutModuleNumber(), is("Test [Tag]"));
    }

    @Test
    public void labelShouldNotContainModuleNUmberAndVersionInfo() {
        ModuleLabel ml = new ModuleLabel(ModuleNumber.moduleNumber(12).get(), new ModuleName("Test"), Optional.<VersionTag>absent());
        assertThat(ml.buildLabelWithoutModuleNumber(), is("Test"));        
    }

}
