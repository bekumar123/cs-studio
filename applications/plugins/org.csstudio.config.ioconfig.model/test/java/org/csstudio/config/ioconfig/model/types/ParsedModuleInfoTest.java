package org.csstudio.config.ioconfig.model.types;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IGsdModuleModel2Query;
import org.csstudio.config.ioconfig.model.types.helper.TypesTestData;
import org.junit.Before;
import org.junit.Test;

public class ParsedModuleInfoTest {

    private ParsedModuleInfo parsedModuleInfo;

    @Before
    public void setUp() {
        Map<Integer, IGsdModuleModel2Query> moduleInfo;
        moduleInfo = new HashMap<Integer, IGsdModuleModel2Query>();
        moduleInfo.put(1, TypesTestData.createGsdModuleModel2(1, "Test1", 10));
        moduleInfo.put(2, TypesTestData.createGsdModuleModel2(2, "Test2", 20));
        moduleInfo.put(3, TypesTestData.createGsdModuleModel2(3, "Test3", 30));
        parsedModuleInfo = new ParsedModuleInfo(moduleInfo);
    }

    @Test
    public void shouldContain3Entries() {
        assertThat(parsedModuleInfo.getModuleInfo().size(), is(3));
    }

    @Test
    public void shouldReturnAllEntries() {
        assertThat(parsedModuleInfo.getModuleInfo(ModuleNumber.moduleNumber(1).get()).getModuleName().getValue(),
                is("Test1"));
        assertThat(parsedModuleInfo.getModuleInfo(ModuleNumber.moduleNumber(2).get()).getModuleName().getValue(),
                is("Test2"));
        assertThat(parsedModuleInfo.getModuleInfo(ModuleNumber.moduleNumber(3).get()).getModuleName().getValue(),
                is("Test3"));
    }
    
}
