package org.csstudio.config.ioconfig.model.pbmodel;

import org.csstudio.config.ioconfig.model.types.ModuleNumber;

public interface GSDModuleDataProvider {

    GSDModuleDBO getPrototypeModule(ModuleNumber moduleNumber);

}
