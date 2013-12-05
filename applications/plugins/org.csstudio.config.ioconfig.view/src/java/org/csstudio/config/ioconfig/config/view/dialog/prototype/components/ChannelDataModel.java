package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;

public interface ChannelDataModel {

    GSDModuleDBO getPrototypeModule();

    boolean isHasInputFields();

    boolean isHasOutputFields();

    boolean isWordSize();

    ArrayList<ModuleChannelPrototypeDBO> getInputChannelPrototypeModelList();

    ArrayList<ModuleChannelPrototypeDBO> getOutputChannelPrototypeModelList();
    
    void addModuleChannelPrototype(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype);
    
    void removeModuleChannelPrototype(@Nonnull final ModuleChannelPrototypeDBO moduleChannelPrototype);
}
