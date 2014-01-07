package org.csstudio.config.ioconfig.model.types;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IGsdModuleModel2Query;

import com.google.common.base.Preconditions;

/*
 * Encapsulate the most important data about a GSD-Module.
 */
public class ModuleInfo {

    private final ModuleNumber moduleNumber;
    
    private final ModuleName moduleName;

    private final SlaveCfgDataList slaveCfgDataList;
    
    ModuleInfo (final IGsdModuleModel2Query gsdModuleModel2) {
 
        Preconditions.checkNotNull(gsdModuleModel2, "gsdModuleModel2 must not be null");
        
        this.moduleNumber = ModuleNumber.moduleNumber(gsdModuleModel2.getModuleNumber()).get();
        this.moduleName = new ModuleName(gsdModuleModel2.getName());
                
        slaveCfgDataList = new SlaveCfgDataList(gsdModuleModel2.getValue());
        
    }

    public SlaveCfgDataList getSlaveCfgDataList() {
        return slaveCfgDataList;
    }

    public boolean isWordSize() {
        return slaveCfgDataList.isWordSize();
    }

    public ModuleNumber getModuleNumber() {
        return moduleNumber;
    }

    public boolean isHasInputs() {
        return slaveCfgDataList.isInput();
    }

    public boolean isHasOutputs() {
        return slaveCfgDataList.isOutput();
    }

    public ModuleName getModuleName() {
        return moduleName;
    }
    
}
