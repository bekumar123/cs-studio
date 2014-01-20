package org.csstudio.config.ioconfig.model.types;

import java.util.List;

import com.google.common.base.Preconditions;

/*
 * A ConfiguredModuleList contains the ModuleIDs from those protoypes that are already configured.
 * A configured module is a module that has channel names.
 */
public class ConfiguredModuleList {

    private final List<ModuleId> protoTypeList;

    public ConfiguredModuleList(final List<ModuleId> protoTypeList) {
        
        Preconditions.checkNotNull(protoTypeList, "protoTypeList must not be null");
        
        this.protoTypeList = protoTypeList;
    }

    public boolean isPresent(ModuleId moduleId) {

        Preconditions.checkNotNull(moduleId, "moduleId must not be null");

        for (ModuleId protoModuleId : protoTypeList) {
            if (protoModuleId.getValue().intValue() == moduleId.getValue().intValue()) {
                return true;
            }
        }
        return false;
    }
    
  
}
