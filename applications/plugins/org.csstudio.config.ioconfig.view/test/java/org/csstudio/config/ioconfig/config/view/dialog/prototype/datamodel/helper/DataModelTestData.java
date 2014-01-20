package org.csstudio.config.ioconfig.config.view.dialog.prototype.datamodel.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDataProvider;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IGsdModuleModel2Query;
import org.csstudio.config.ioconfig.model.types.ConfiguredModuleList;
import org.csstudio.config.ioconfig.model.types.ModuleId;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.csstudio.config.ioconfig.model.types.ParsedModuleInfo;
import org.csstudio.config.ioconfig.model.types.PrototypeList;

public class DataModelTestData {

    public static IGsdModuleModel2Query createGsdModuleModel2(final Integer moduleNumber, final String name,
            final Integer... value) {
        return new IGsdModuleModel2Query() {
            @Override
            public List<Integer> getValue() {
                return DataModelTestData.createIntegerListWithValues(value);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Integer getModuleNumber() {
                return moduleNumber;
            }
        };
    }

    public static List<Integer> createIntegerListWithValues(final Integer... value) {
        ArrayList<Integer> valueList = new ArrayList<Integer>();
        for (int i = 0; i < value.length; i++) {
            valueList.add(value[i]);
        }
        return valueList;
    }

    public static PrototypeList createPrototypeList() {
        return new PrototypeList(createModuleList(), createConfiguredModuleList());
    }
    
    public static ConfiguredModuleList createConfiguredModuleList() {
        List<ModuleId> moduleIds = new ArrayList<ModuleId>();
        moduleIds.add(new ModuleId(20));
        moduleIds.add(new ModuleId(30));
        moduleIds.add(new ModuleId(40));
        return new ConfiguredModuleList(moduleIds);
    }

    public static ParsedModuleInfo createParsedModuleInfo() {
        Map<Integer, IGsdModuleModel2Query> moduleInfo = new HashMap<Integer, IGsdModuleModel2Query>();
        moduleInfo.put(210, createGsdModuleModel2(210, "Test 20", 2));
        moduleInfo.put(310, createGsdModuleModel2(310, "Test 30", 2));
        moduleInfo.put(410, createGsdModuleModel2(410, "Test 40", 2));
        moduleInfo.put(510, createGsdModuleModel2(510, "Test 50", 2));
        moduleInfo.put(610, createGsdModuleModel2(610, "Test 60", 2));
        moduleInfo.put(710, createGsdModuleModel2(710, "Test 70", 2));
        return new ParsedModuleInfo(moduleInfo);
    }

    public static List<GSDModuleDBOReadOnly> createModuleList() {
        List<GSDModuleDBOReadOnly> modules = new ArrayList<GSDModuleDBOReadOnly>();
        modules.add(createGSDMOduleReadOnly(20, "Test 20", 210));
        modules.add(createGSDMOduleReadOnly(30, "Test 30", 310));
        modules.add(createGSDMOduleReadOnly(40, "Test 40", 410));
        return modules;
    }

    public static GSDModuleDataProvider createSelectedSlave() {

        return new GSDModuleDataProvider() {

            private Map<ModuleNumber, GSDModuleDBO> modules;

            @Override
            public void registerNewPrototype(GSDModuleDBO gsdModules) {
                init();
                // TODO Auto-generated method stub
            }

            @Override
            public void refreshProtoype(ModuleNumber moduleNumber) {
                // do nothing
            }

            @Override
            public GSDModuleDBO getPrototype(ModuleNumber moduleNumber) {
                init();
                return new GSDModuleDBO();
            }

            private void init() {
                if (modules != null) {
                    return;
                }

            }
        };
    }

    public static GSDModuleDBOReadOnly createGSDMOduleReadOnly(final int id, final String name, final int moduleNumber) {
        return new GSDModuleDBOReadOnly() {

            @Override
            public boolean moduleNameContains(String filterText) {
                return false;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ModuleNumber getModuleNumber() {
                return ModuleNumber.moduleNumber(moduleNumber).get();
            }

            @Override
            public ModuleLabel getModuleLabel() {
                return null;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public GSDModuleDBO cloneAsNewVersion(ModuleNumber nextVersionedModuleNumber,
                    ModuleVersionInfo moduleVersionInfo) {
                return null;
            }
        };
    }
}
