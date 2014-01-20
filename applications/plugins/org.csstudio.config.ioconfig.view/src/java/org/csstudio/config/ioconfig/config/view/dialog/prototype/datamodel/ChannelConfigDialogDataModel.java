package org.csstudio.config.ioconfig.config.view.dialog.prototype.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDataProvider;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.types.ModuleInfo;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleName;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.csstudio.config.ioconfig.model.types.ParsedModuleInfo;
import org.csstudio.config.ioconfig.model.types.PrototypeList;
import org.csstudio.config.ioconfig.model.types.SlaveCfgDataList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ChannelConfigDialogDataModel implements IChannelDataModel {

    private GSDModuleDataProvider selectedSlave;
    private final PrototypeList prototypeList;
    private final ParsedModuleInfo parsedModuleInfo;

    private GSDModuleDBO prototype;

    private ArrayList<ModuleChannelPrototypeDBO> inputChannelPrototypeModelList;
    private ArrayList<ModuleChannelPrototypeDBO> outputChannelPrototypeModelList;
    private ModuleNumber currentModuleNumber;

    private boolean hasInputFields;
    private boolean hasOutputFields;
    private boolean isWordSize;

    // Used to store the version info that has currently been changed.
    // Needed since we want to assign the version data
    // only if the entry is saved.
    private Optional<ModuleVersionInfo> editedModuleVersionInfo;

    //@formatter:off
    public ChannelConfigDialogDataModel(
            final GSDModuleDataProvider selectedSlave, 
            final PrototypeList prototypeList,
            final ParsedModuleInfo parsedModuleInfo) {
            //@formatter:on

        Preconditions.checkNotNull(selectedSlave, "selectedSlave must not be null");
        Preconditions.checkNotNull(prototypeList, "prototypeList must not be null");
        Preconditions.checkNotNull(parsedModuleInfo, "parsedModuleInfo must not be null");

        this.selectedSlave = selectedSlave;
        this.prototypeList = prototypeList;
        this.parsedModuleInfo = parsedModuleInfo;

        refreshDataModel(prototypeList.getFirstModuleNumber());
    }

    public void refresh(final Optional<ModuleNumber> moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        if (moduleNumber.isPresent()) {
            refreshDataModel(moduleNumber.get());
        } else {
            hasInputFields = false;
            hasOutputFields = false;
            inputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
            outputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        }
    }

    public void save() throws PersistenceException {
        if (editedModuleVersionInfo.isPresent()) {
            
            if (!editedModuleVersionInfo.get().getModuleNumber().equals(getCurrentModuleNumber())) {
                throw new IllegalStateException("moduleNumbers must not differ.");
            }
            
            getPrototypeModule().setVersionTag(editedModuleVersionInfo.get().getVersionTag().getValue());
            getPrototypeModule().setVersionNote(editedModuleVersionInfo.get().getVersionNote().getValue());
        }
        
        setEmptyChannelPrototypeName2Unused();
        
        prototype.assingUpdated();

        prototype.save();
        
        editedModuleVersionInfo = Optional.absent();
        this.prototypeList.unsetEditedModuleVersionInfo();
        refreshFromRepository(); 
    }

    public void undo() throws PersistenceException {
               
        removeEmptyEntries(inputChannelPrototypeModelList);
        removeEmptyEntries(outputChannelPrototypeModelList);
        
        editedModuleVersionInfo = Optional.absent();
        this.prototypeList.unsetEditedModuleVersionInfo();
        refreshFromRepository();
    }

    public GSDModuleDBO getPrototypeModule() {
        return prototype;
    }

    public ModuleVersionInfo getModuleVersionInfo() {
        if (editedModuleVersionInfo.isPresent()) {
            if (!getCurrentModuleNumber().equals(editedModuleVersionInfo.get().getModuleNumber())) {
                throw new IllegalStateException("moduleNumbers are different");
            }
            return editedModuleVersionInfo.get();
        }
        return ModuleVersionInfo.build(getCurrentModuleNumber(), prototype.getVersionTag(), prototype.getVersionNote());
    }

    public void updateModuleVersionInfo(final ModuleVersionInfo moduleVersionInfo) {

        Preconditions.checkNotNull(moduleVersionInfo, "moduleVersionInfo must not be null");

        this.editedModuleVersionInfo = Optional.of(moduleVersionInfo);
        this.prototypeList.setEditedModuleVersionInfo(moduleVersionInfo);
    }

    public ModuleLabel getModuleLabel() {
        ModuleVersionInfo moduleVersionInfo = getModuleVersionInfo();
        return moduleVersionInfo.getModuleLabel(getModuleName());
    }

    public GSDModuleDBO createNewVersion(final ModuleVersionInfo moduleVersionInfo) throws PersistenceException {

        Preconditions.checkNotNull(moduleVersionInfo, "moduleVersionInfo must not be null");
        Preconditions.checkArgument(!moduleVersionInfo.getModuleNumber().isVersioned(),
                "moduleNumber must not be versioned");

        GSDModuleDBOReadOnly originGSDModuleDBO = prototypeList.getModule(moduleVersionInfo.getModuleNumber());
        
        //@formatter:off
        GSDModuleDBO versionedGSDModuleDBO = originGSDModuleDBO.cloneAsNewVersion(
                prototypeList.createNextVersion(moduleVersionInfo.getModuleNumber()),
                moduleVersionInfo);
                //@formatter:on
        
        prototypeList.add(versionedGSDModuleDBO);
                
        // make sure that the Protoype instance knows about the new entry.
        selectedSlave.registerNewPrototype(versionedGSDModuleDBO);
        
        return versionedGSDModuleDBO;
    }

    public SlaveCfgDataList getSlaveCfgDataList() {
        ModuleInfo moduleInfo = parsedModuleInfo.getModuleInfo(currentModuleNumber);
        return moduleInfo.getSlaveCfgDataList();
    }

    public ParsedModuleInfo getParsedModuleInfo() {
        return parsedModuleInfo;
    }

    public PrototypeList getPrototypeList() throws PersistenceException {
        return prototypeList;
    }

    public ModuleNumber getCurrentModuleNumber() {
        return currentModuleNumber;
    }

    public boolean isHasInputFields() {
        return hasInputFields;
    }

    public boolean isHasOutputFields() {
        return hasOutputFields;
    }

    public boolean isWordSize() {
        return isWordSize;
    }

    public boolean hasData() {
        return isHasInputFields() || isHasOutputFields();
    }

    private ModuleName getModuleName() {
        return prototypeList.getModuleName(getCurrentModuleNumber());
    }

    private void setEmptyChannelPrototypeName2Unused() {
        final Set<ModuleChannelPrototypeDBO> moduleChannelPrototype;
        moduleChannelPrototype = getPrototypeModule().getModuleChannelPrototype();
        if (moduleChannelPrototype != null) {
            for (final ModuleChannelPrototypeDBO moduleChannelPrototypeDBO : moduleChannelPrototype) {
                String name = moduleChannelPrototypeDBO.getName();
                if (name == null || name.isEmpty()) {
                    name = "unused"; //$NON-NLS-1$
                    moduleChannelPrototypeDBO.setName(name);
                }
            }
        }
    }

    private void removeEmptyEntries(final ArrayList<ModuleChannelPrototypeDBO> list) {
        Iterator<ModuleChannelPrototypeDBO> inputIterator = new ArrayList<ModuleChannelPrototypeDBO>(list).iterator();
        while (inputIterator.hasNext()) {
            ModuleChannelPrototypeDBO entry = inputIterator.next();
            if (Strings.isNullOrEmpty(entry.getName()) || (entry.getId() == 0)) {
                list.remove(entry);
                getPrototypeModule().removeModuleChannelPrototype(entry);
            }
        }
    }

    private void refreshDataModel(final ModuleNumber moduleNumber) {

        editedModuleVersionInfo = Optional.absent();

        currentModuleNumber = moduleNumber;

        inputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        outputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();

        prototype = selectedSlave.getPrototype(moduleNumber);
        
        if (prototype == null) {
            throw new IllegalStateException("Prototype must not be null");
        }

        // Don't directly access the channel-data of the Prototype. This makes it easier
        // to handle a cancel operation.
        if (prototype.getModuleChannelPrototypeNH() != null) {
            for (final ModuleChannelPrototypeDBO moduleChannelPrototype : prototype.getModuleChannelPrototypeNH()) {
                if (moduleChannelPrototype.isInput()) {
                    inputChannelPrototypeModelList.add(moduleChannelPrototype);
                } else {
                    outputChannelPrototypeModelList.add(moduleChannelPrototype);
                }
            }
        }
        
        ModuleInfo moduleInfo = parsedModuleInfo.getModuleInfo(currentModuleNumber);

        hasInputFields = moduleInfo.isHasInputs();
        hasOutputFields = moduleInfo.isHasOutputs();
        isWordSize = moduleInfo.isWordSize();
        
    }

    private void refreshFromRepository() throws PersistenceException {
        selectedSlave.refreshProtoype(getCurrentModuleNumber());
    }

    public ArrayList<ModuleChannelPrototypeDBO> getInputChannelPrototypeModelList() {
        return inputChannelPrototypeModelList;
    }

    public ArrayList<ModuleChannelPrototypeDBO> getOutputChannelPrototypeModelList() {
        return outputChannelPrototypeModelList;
    }

    @Override
    public void addModuleChannelPrototype(ModuleChannelPrototypeDBO moduleChannelPrototype) {
        getPrototypeModule().addModuleChannelPrototype(moduleChannelPrototype);
    }
   
    @Override
    public void removeModuleChannelPrototype(ModuleChannelPrototypeDBO moduleChannelPrototype) {
        getPrototypeModule().removeModuleChannelPrototype(moduleChannelPrototype);        
    }

}
