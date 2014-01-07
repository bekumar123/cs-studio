package org.csstudio.config.ioconfig.model.types;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IGsdModuleModel2Query;
import org.csstudio.config.ioconfig.model.types.helper.TypesTestData;
import org.junit.Test;

public class ModuleInfoTest {

    @Test
    public void testHasNoInputs() {
        IGsdModuleModel2Query gsdModuleModel2 = TypesTestData.createGsdModuleModel2(1, "test", 2);
        ModuleInfo moduleInfo = new ModuleInfo(gsdModuleModel2);
        assertThat(moduleInfo.isHasInputs(), is(false));        
    }

    @Test
    public void testHasNoOutputs() {
        IGsdModuleModel2Query gsdModuleModel2 = TypesTestData.createGsdModuleModel2(1, "test", 2);
        ModuleInfo moduleInfo = new ModuleInfo(gsdModuleModel2);
        assertThat(moduleInfo.isHasOutputs(), is(false));        
    }

    @Test
    public void testIsNotWordSize() {
        IGsdModuleModel2Query gsdModuleModel2 = TypesTestData.createGsdModuleModel2(1, "test", 2);
        ModuleInfo moduleInfo = new ModuleInfo(gsdModuleModel2);
        assertThat(moduleInfo.isWordSize(), is(false));        
    }

    @Test
    public void testIsWordSizeOneParameter() {
        IGsdModuleModel2Query gsdModuleModel2 = TypesTestData.createGsdModuleModel2(1, "test", 112);
        ModuleInfo moduleInfo = new ModuleInfo(gsdModuleModel2);
        assertThat(moduleInfo.isWordSize(), is(true));        
    }

    @Test
    public void testIsWordSizeTwoParameter() {
        IGsdModuleModel2Query gsdModuleModel2 = TypesTestData.createGsdModuleModel2(1, "test", 4, 64);
        ModuleInfo moduleInfo = new ModuleInfo(gsdModuleModel2);
        assertThat(moduleInfo.isWordSize(), is(true));        
    }

}
