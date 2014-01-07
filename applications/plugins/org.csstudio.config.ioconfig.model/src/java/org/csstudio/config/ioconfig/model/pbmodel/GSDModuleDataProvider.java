package org.csstudio.config.ioconfig.model.pbmodel;

import org.csstudio.config.ioconfig.model.types.ModuleNumber;

public interface GSDModuleDataProvider {

    GSDModuleDBO getPrototype(ModuleNumber moduleNumber);

    void registerNewPrototype(GSDModuleDBO gsdModules);

    void refreshProtoype(ModuleNumber moduleNumber);

}
