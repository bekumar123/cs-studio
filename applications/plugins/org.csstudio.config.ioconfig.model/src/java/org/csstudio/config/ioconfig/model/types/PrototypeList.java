package org.csstudio.config.ioconfig.model.types;

import java.util.List;

import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;

import com.google.common.base.Optional;

public class PrototypeList {

    private final List<ModuleChannelPrototypeDBO> protoTypeList;

    public PrototypeList(List<ModuleChannelPrototypeDBO> protoTypeList) {
        this.protoTypeList = protoTypeList;
    }

    public boolean hasPrototype(ModuleId moduleId) {
        for (ModuleChannelPrototypeDBO moduleChannelPrototypeDBO : protoTypeList) {
            if (moduleChannelPrototypeDBO.getGSDModule().getId() == moduleId.getValue()) {
                return true;
            }
        }
        return false;
    }
    
  
}
