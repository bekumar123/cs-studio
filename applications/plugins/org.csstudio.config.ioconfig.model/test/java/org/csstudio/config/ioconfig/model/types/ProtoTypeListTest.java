package org.csstudio.config.ioconfig.model.types;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.types.helper.TypesTestData;
import org.junit.Before;
import org.junit.Test;

public class ProtoTypeListTest {

    private PrototypeList prototypeList;

    @Before
    public void setUp() {
        prototypeList = new PrototypeList(TypesTestData.createModuleList(), TypesTestData.createConfiguredModuleList());
    }

    @Test
    public void shouldGetFirstEntry() {
        assertThat(prototypeList.getFirstModuleNumber().getValue().intValue(), is(310));
    }

    @Test
    public void shouldSort() {
        prototypeList.sort();
        assertThat(prototypeList.getFirstModuleNumber().getValue().intValue(), is(210));
    }

    @Test
    public void shouldCetMissingModules() {
        List<GSDModuleDBO> createMissingModules = prototypeList.createMissingModules(
                TypesTestData.createParsedModuleInfo(), new GSDFileDBO());
        assertThat(createMissingModules.size(), is(3));
        assertThat(createMissingModules.get(0).getModuleNumber().getValue(), is(510));
        assertThat(createMissingModules.get(1).getModuleNumber().getValue(), is(610));
        assertThat(createMissingModules.get(2).getModuleNumber().getValue(), is(710));
    }

    @Test
    public void shouldHaveChannelConfiguration() {
        assertThat(prototypeList.hasChannelConfiguration(ModuleNumber.moduleNumber(210).get()), is(true));
    }
    
    public void shouldNotHaveChannelConfiguration() {
        assertThat(prototypeList.hasChannelConfiguration(ModuleNumber.moduleNumber(710).get()), is(false));        
    }
        
    @Test
    public void shouldGetNextVersionedModuleNumber() {
        ModuleNumber nextVersionedModuleNumber = prototypeList.createNextVersion(ModuleNumber.moduleNumber(210).get());
        assertThat(nextVersionedModuleNumber.getModuleNumberWithoutVersionInfo(), is(210));        
    }

}
