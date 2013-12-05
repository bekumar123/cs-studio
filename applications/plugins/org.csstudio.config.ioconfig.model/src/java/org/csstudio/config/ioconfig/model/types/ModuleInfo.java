package org.csstudio.config.ioconfig.model.types;

import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;

public class ModuleInfo {

    private final ModuleNumber moduleNumber;
    
    private final ModuleName moduleName;

    private final boolean hasInputs;

    private final boolean hasOutputs;

    private final boolean isWordSize;

    private final List<SlaveCfgData> slaveCfgDataList;
    
    //@formatter:off
    public ModuleInfo(final ModuleNumber moduleNumber, 
            final ModuleName moduleName,
            final boolean hasInputs, 
            final boolean hasOutputs, 
            final boolean isWordSize,
            final List<SlaveCfgData> slaveCfgDataList) {
            //@formatter:on
        this.moduleNumber = moduleNumber;
        this.moduleName = moduleName;
        this.hasInputs = hasInputs;
        this.hasOutputs = hasOutputs;
        this.isWordSize = isWordSize;
        this.slaveCfgDataList = slaveCfgDataList;
    }

    public List<SlaveCfgData> getSlaveCfgDataList() {
        return slaveCfgDataList;
    }

    public boolean isWordSize() {
        return isWordSize;
    }

    public ModuleNumber getModuleNumber() {
        return moduleNumber;
    }

    public boolean isHasInputs() {
        return hasInputs;
    }

    public boolean isHasOutputs() {
        return hasOutputs;
    }

    public ModuleName getModuleName() {
        return moduleName;
    }
    
}
