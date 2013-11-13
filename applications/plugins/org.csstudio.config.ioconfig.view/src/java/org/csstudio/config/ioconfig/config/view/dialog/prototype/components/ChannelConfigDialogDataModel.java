package org.csstudio.config.ioconfig.config.view.dialog.prototype.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgData;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveCfgDataBuilder;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;
import org.csstudio.config.ioconfig.model.types.ModuleList;
import org.csstudio.config.ioconfig.model.types.ModuleNumber;

import com.google.common.base.Strings;

public class ChannelConfigDialogDataModel {
    
    private SlaveDBO selectedSlave;
    private final ModuleList moduleList;
    private final Map<Integer, GsdModuleModel2> moduleMap;
    
    private GSDModuleDBO prototype;

    private ArrayList<ModuleChannelPrototypeDBO> inputChannelPrototypeModelList;
    private ArrayList<ModuleChannelPrototypeDBO> outputChannelPrototypeModelList;
    private ModuleNumber currentModuleNumber;

    private boolean hasInputFields;
    private boolean hasOutputFields;
    private boolean isWordSize = true;
    private boolean isNew = false;

    public ChannelConfigDialogDataModel(final SlaveDBO selectedSlave, final ModuleList moduleList) {
        super();
        this.selectedSlave = selectedSlave;
        this.moduleList = moduleList;
        this.moduleMap = selectedSlave.getGSDFile().getParsedGsdFileModel().getModuleMap();
        currentModuleNumber = moduleList.getFirstModuleNumber().get();
        System.out.println(currentModuleNumber);
        updateDataModel(currentModuleNumber);
    }

    public void refreshDataModel(ModuleNumber moduleNumber) {
        updateDataModel(moduleNumber);
    }

    public void save() throws PersistenceException {
       getPrototypeModule().save();
       Repository.refresh(selectedSlave.getGSDFile());
    }
    
    public void undo() throws PersistenceException {
        removeEmptyEntries(inputChannelPrototypeModelList);
        removeEmptyEntries(outputChannelPrototypeModelList);
        Repository.refresh(selectedSlave.getGSDFile());        
    }
    
    public GSDModuleDBO getPrototypeModule() {
        return prototype;
    }

    public List<SlaveCfgData> getSlaveCfgDataList() {
        GsdModuleModel2 gsdModuleModel2 = moduleMap.get(currentModuleNumber.getModuleNumberWithoutVersionInfo());
        return new SlaveCfgDataBuilder(gsdModuleModel2.getValue()).getSlaveCfgDataList();     
    }
    
    public GSDFileDBO getGsdFileDBO() {
        return selectedSlave.getGSDFile().getParsedGsdFileModel().getGsdFileDBO();        
    }
    
    public ModuleList getModulelist() throws PersistenceException {
        return moduleList;
    }
    
    public ModuleNumber getCurrentModuleNumber() {
        return currentModuleNumber;
    }

    public String getModuleName() {
        return moduleList.getModuleName(getCurrentModuleNumber()).getValue();
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

    public boolean isNew() {
        return isNew;
    }

    private void removeEmptyEntries(ArrayList<ModuleChannelPrototypeDBO> list) {
        Iterator<ModuleChannelPrototypeDBO> inputIterator = new ArrayList<ModuleChannelPrototypeDBO>(list).iterator();
        while (inputIterator.hasNext()) {
            ModuleChannelPrototypeDBO entry = inputIterator.next();
            if (Strings.isNullOrEmpty(entry.getName()) || (entry.getId() == 0)) {
                list.remove(entry);
                getPrototypeModule().removeModuleChannelPrototype(entry);
            }
        }
    }
    
    private void updateDataModel(ModuleNumber moduleNumber) {

        inputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();
        outputChannelPrototypeModelList = new ArrayList<ModuleChannelPrototypeDBO>();

        prototype = selectedSlave.getGSDFile().getParsedGsdFileModel().getGsdFileDBO().getGSDModule(moduleNumber.getValue());

        if (prototype == null) {
            isNew = true;
            prototype = new GSDModuleDBO(moduleList.getModuleName(moduleNumber).getValue());
            prototype.setGSDFile(getGsdFileDBO());
            prototype.setModuleId(moduleNumber.getValue());
            prototype.setCreationData("Roger", new Date());
        } else {
            isNew = false;
            prototype.setUpdatedBy("Roger");            
            prototype.setUpdatedOn(new Date());
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
        
        hasInputFields = false;
        hasOutputFields = false;

        for (final SlaveCfgData slaveCfgData : getSlaveCfgDataList()) {            
            if (slaveCfgData.isWordSize()) {
                isWordSize &= true;
            } else {
                isWordSize &= false;
            }
            hasInputFields = hasInputFields || slaveCfgData.isInput();
            hasOutputFields = hasOutputFields || slaveCfgData.isOutput();
        }

    }

    public ArrayList<ModuleChannelPrototypeDBO> getInputChannelPrototypeModelList() {
        return inputChannelPrototypeModelList;
    }

    public ArrayList<ModuleChannelPrototypeDBO> getOutputChannelPrototypeModelList() {
        return outputChannelPrototypeModelList;
    }

    
}
