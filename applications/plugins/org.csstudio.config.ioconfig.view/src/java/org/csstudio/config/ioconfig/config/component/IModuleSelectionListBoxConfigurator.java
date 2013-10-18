package org.csstudio.config.ioconfig.config.component;

public interface IModuleSelectionListBoxConfigurator {
    IModuleSelectionListBoxConfigurator ignoreModulesWithoutPrototype();
    IModuleSelectionListBoxConfigurator readOnly();
    IModuleSelectionListBoxConfigurator autoFilter();
}
