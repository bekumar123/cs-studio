package org.csstudio.config.ioconfig.config.component;

public class ModuleSelectionListBoxConfigurator implements IModuleSelectionListBoxConfig, IModuleSelectionListBoxConfigurator {

    private boolean ignoreModulesWithoutPrototype = false;
    private boolean readOnly = false;
    private boolean autoFilter = false;

    ModuleSelectionListBoxConfigurator() {
    }

    public ModuleSelectionListBoxConfigurator ignoreModulesWithoutPrototype() {
        this.ignoreModulesWithoutPrototype = true;
        return this;
    }

    public boolean isIgnoreModulesWithoutPrototype() {
        return ignoreModulesWithoutPrototype;
    }

    @Override
    public ModuleSelectionListBoxConfigurator readOnly() {
        this.readOnly = true;
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public IModuleSelectionListBoxConfigurator autoFilter() {
        autoFilter = true;
        return this;
    }

    @Override
    public boolean isAutoFilter() {
        return autoFilter;
    }
    
}
