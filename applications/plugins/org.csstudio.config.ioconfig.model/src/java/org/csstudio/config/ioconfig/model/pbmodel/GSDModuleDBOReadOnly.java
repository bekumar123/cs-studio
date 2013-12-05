package org.csstudio.config.ioconfig.model.pbmodel;

import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;

public interface GSDModuleDBOReadOnly {

    int getId();
    
    ModuleNumber getModuleNumber();

    String getName();
    
    boolean moduleNameContains(String filterText);

    ModuleLabel getModuleLabel();

    GSDModuleDBO cloneNewVersion(ModuleNumber nextVersionedModuleNumber, ModuleVersionInfo moduleVersionInfo);
    
}
