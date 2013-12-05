package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;

public class ModuleLabel {

    private final ModuleNumber moduleNumber;

    private final ModuleName moduleName;

    private final Optional<VersionTag> versionTag;
    
    //@formatter:off
    ModuleLabel(
            final ModuleNumber moduleNumber, 
            final ModuleName moduleName, 
            final Optional<VersionTag> versionTag) {
            //@formatter:on
        
        this.moduleNumber = moduleNumber;
        this.moduleName = moduleName;
        this.versionTag = versionTag;
    }

    public String buildLabel() {
        String moduleNumberAsString = String.valueOf(moduleNumber.getModuleNumberWithoutVersionInfo());
        return moduleNumberAsString + " : " + buildLabelWithoutModuleNumber();
    }

    public String buildLabelWithoutModuleNumber() {
        if (versionTag.isPresent()) {
            return moduleName.getValue() + " " + "[" + versionTag.get().getValue() + "]";
        } else {
            return moduleName.getValue();
        }
    }

}
