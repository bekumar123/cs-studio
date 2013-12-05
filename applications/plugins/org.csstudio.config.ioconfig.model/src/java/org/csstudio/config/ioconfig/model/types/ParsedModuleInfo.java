package org.csstudio.config.ioconfig.model.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;

import com.google.common.base.Preconditions;

public class ParsedModuleInfo {
    
    private final Map<Integer, GsdModuleModel2> moduleInfo;

    public ParsedModuleInfo(Map<Integer, GsdModuleModel2> moduleInfo) {
        
        Preconditions.checkNotNull(moduleInfo, "moduleInfo must not be null");
        Preconditions.checkArgument(moduleInfo.size() > 0, "size must be > 0");
        
        this.moduleInfo = moduleInfo;
    }
    
    public List<ModuleInfo> getModuleInfo() {
        List<ModuleInfo> infos = new ArrayList<ModuleInfo>();
        for (Entry<Integer, GsdModuleModel2> gsdModuleModel2 : moduleInfo.entrySet()) {
            infos.add(createModuleInfo(gsdModuleModel2.getValue()));            
        }
        return infos;
    }
    
    public ModuleInfo getModuleInfo(final ModuleNumber moduleNumber) {
        return createModuleInfo(moduleInfo.get(moduleNumber.getModuleNumberWithoutVersionInfo()));            
    }
    
    private ModuleInfo createModuleInfo(GsdModuleModel2 gsdModuleModel2) {
        
        boolean input = false;
        boolean output = false;
        boolean isWordSize = false;
        
        final SlaveCfgDataBuilder slaveCfgDataFactory = new SlaveCfgDataBuilder(gsdModuleModel2.getValue());
        for (final SlaveCfgData slaveCfgData : slaveCfgDataFactory.getSlaveCfgDataList()) {
            
            if (slaveCfgData.isWordSize()) {
                isWordSize &= true;
            } else {
                isWordSize &= false;
            }

            input |= slaveCfgData.isInput();
            output |= slaveCfgData.isOutput();
        }
   
        //@formatter:off
        return new ModuleInfo(
                ModuleNumber.moduleNumber(gsdModuleModel2.getModuleNumber()).get(),
                new ModuleName(gsdModuleModel2.getName()),
                input,
                output,
                isWordSize,
                slaveCfgDataFactory.getSlaveCfgDataList());
                //@formatter:on

    }
}