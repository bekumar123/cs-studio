package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.AbstractHibernateManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDataProvider;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.types.ModuleInfo;
import org.csstudio.config.ioconfig.model.types.ModuleLabel;
import org.csstudio.config.ioconfig.model.types.ModuleList;
import org.csstudio.config.ioconfig.model.types.ModuleName;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;
import org.csstudio.config.ioconfig.model.types.ModuleVersionInfo;
import org.csstudio.config.ioconfig.model.types.ParsedModuleInfo;
import org.csstudio.config.ioconfig.model.types.RepositoryRefreshable;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ChannelConfigDialogDataModel implements ChannelDataModel {

    private final RepositoryRefreshable gsdFileDBO;
    private GSDModuleDataProvider selectedSlave;
    private final ModuleList moduleList;
    private final ParsedModuleInfo parsedModuleInfo;

    private GSDModuleDBO prototype;

    private ArrayList<ModuleChannelPrototypeDBO> inputChannelPrototypeModelList;
    private ArrayList<ModuleChannelPrototypeDBO> outputChannelPrototypeModelList;
    private ModuleNumber currentModuleNumber;

    private boolean hasInputFields;
    private boolean hasOutputFields;
    private boolean isWordSize = true;

    private Optional<ModuleVersionInfo> editedModuleVersionInfo;

    //@formatter:off
    public ChannelConfigDialogDataModel(
            final RepositoryRefreshable gsdFileDBO,
            final GSDModuleDataProvider selectedSlave, 
            final ModuleList moduleList,
            final ParsedModuleInfo parsedModuleInfo) {
            //@formatter:on

        Preconditions.checkNotNull(gsdFileDBO, "gsdFileDBO must not be null");
        Preconditions.checkNotNull(selectedSlave, "selectedSlave must not be null");
        Preconditions.checkNotNull(moduleList, "moduleList must not be null");
        Preconditions.checkNotNull(parsedModuleInfo, "parsedModuleInfo must not be null");

        this.gsdFileDBO = gsdFileDBO;
        this.selectedSlave = selectedSlave;
        this.moduleList = moduleList;
        this.parsedModuleInfo = parsedModuleInfo;

        refreshDataModel(moduleList.getFirstModuleNumber());
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
        
        prototype.initUpdated();

        prototype.save();
        
        editedModuleVersionInfo = Optional.absent();
        this.moduleList.unsetEditedModuleVersionInfo();
        refreshFromRepository(); 
    }

    public void undo() throws PersistenceException {
               
        removeEmptyEntries(inputChannelPrototypeModelList);
        removeEmptyEntries(outputChannelPrototypeModelList);
        
        editedModuleVersionInfo = Optional.absent();
        this.moduleList.unsetEditedModuleVersionInfo();
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
        this.moduleList.setEditedModuleVersionInfo(moduleVersionInfo);
    }

    public ModuleLabel getModuleLabel() {
        ModuleVersionInfo moduleVersionInfo = getModuleVersionInfo();
        return moduleVersionInfo.getModuleLabel(new ModuleName(getModuleName()));
    }

    public GSDModuleDBO createNewVersion(final ModuleVersionInfo moduleVersionInfo) throws PersistenceException {

        Preconditions.checkNotNull(moduleVersionInfo, "moduleVersionInfo must not be null");
        Preconditions.checkArgument(!moduleVersionInfo.getModuleNumber().isVersioned(),
                "moduleNumber must not be versioned");

        GSDModuleDBOReadOnly originGSDModuleDBO = moduleList.getModule(moduleVersionInfo.getModuleNumber());
        
        //@formatter:off
        GSDModuleDBO versionedGSDModuleDBO = originGSDModuleDBO.cloneNewVersion(
                moduleList.getNextVersionedModuleNumber(moduleVersionInfo.getModuleNumber()),
                moduleVersionInfo);
                //@formatter:on
        
        Repository.saveOrUpdate(versionedGSDModuleDBO);        
        moduleList.add(versionedGSDModuleDBO);
                
        return versionedGSDModuleDBO;
    }

    public List<SlaveCfgData> getSlaveCfgDataList() {
        ModuleInfo moduleInfo = parsedModuleInfo.getModuleInfo(currentModuleNumber);
        return moduleInfo.getSlaveCfgDataList();
    }

    public ParsedModuleInfo getParsedModuleInfo() {
        return parsedModuleInfo;
    }

    public ModuleList getModulelist() throws PersistenceException {
        return moduleList;
    }

    public ModuleNumber getCurrentModuleNumber() {
        return currentModuleNumber;
    }

    public String getModuleName() {
        return Strings.nullToEmpty(moduleList.getModuleName(getCurrentModuleNumber()).getValue());
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

        prototype = selectedSlave.getPrototypeModule(moduleNumber);
        
        if (prototype == null) {
            throw new IllegalStateException("Prototype must not be null");
        }

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

    public void refreshFromRepository() throws PersistenceException {
        Repository.refresh(gsdFileDBO);
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
