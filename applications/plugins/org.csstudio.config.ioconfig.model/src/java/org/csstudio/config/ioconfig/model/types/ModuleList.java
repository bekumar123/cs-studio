package org.csstudio.config.ioconfig.model.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.GsdModuleModel2;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class ModuleList {

    private final Map<ModuleNumber, GSDModuleDBO> modules;
    private final List<GSDModuleDBO> moduleList;
    private PrototypeList protoTypeList;

    public ModuleList(List<GSDModuleDBO> moduleList, PrototypeList protoTypeList) {
        this.moduleList = new ArrayList<GSDModuleDBO>(moduleList);
        this.protoTypeList = protoTypeList;
        modules = new LinkedHashMap<ModuleNumber, GSDModuleDBO>();
        for (GSDModuleDBO gsdModuleDBO : moduleList) {
            modules.put(ModuleNumber.moduleNumber(gsdModuleDBO.getModuleId()).get(), gsdModuleDBO);
        }
    }

    public Optional<ModuleNumber> getFirstModuleNumber() {
        return ModuleNumber.moduleNumber(moduleList.get(0).getModuleId());
    }

    public boolean isPresent(Optional<ModuleNumber> moduleNumber) {
        if (!moduleNumber.isPresent()) {
            return false;
        }
        if (modules.containsKey(moduleNumber.get())) {
            return true;
        } else {
            return false;
        }
    }

    public Optional<Object> getObject(Optional<ModuleNumber> moduleNumber) {
        if (!isPresent(moduleNumber)) {
            return Optional.absent();
        }
        return Optional.of((Object) modules.get(moduleNumber.get()));
    }

    public Optional<ModuleName> getModuleName(Optional<ModuleNumber> moduleNumber) {
        if (!isPresent(moduleNumber)) {
            return Optional.absent();
        }
        return Optional.of(new ModuleName(modules.get(moduleNumber.get()).getName()));
    }

    public ModuleName getModuleName(ModuleNumber moduleNumber) {
        if (!modules.containsKey(moduleNumber)) {
            throw new IllegalStateException("No module for number " + moduleNumber);
        }
        return new ModuleName(modules.get(moduleNumber).getName());
    }

    public GSDModuleDBO getModule(ModuleNumber moduleNumber) {
        if (!modules.containsKey(moduleNumber)) {
            throw new IllegalStateException("No module for number " + moduleNumber);
        }
        return modules.get(moduleNumber);
    }

    public boolean hasPrototype(Optional<ModuleNumber> moduleNumber) {
        if (!moduleNumber.isPresent()) {
            return false;
        }
        Optional<Integer> moduleId = moduleId(moduleNumber.get());
        if (!moduleId.isPresent()) {
            throw new IllegalStateException("cant get id for module: " + moduleNumber.get());
        }
        return protoTypeList.hasPrototype(new ModuleId(moduleId.get()));
    }

    private Optional<Integer> moduleId(ModuleNumber moduleNumber) {
        for (GSDModuleDBO moduleDBO : moduleList) {
            if (moduleDBO.getModuleNumber().equals(moduleNumber)) {
                return Optional.of(moduleDBO.getId());
            }
        }
        return Optional.absent();
    }

    public ModuleNumber getNextVersionedModuleNumber(ModuleNumber moduleNumber) {

        Integer maxVersion = 0;

        for (GSDModuleDBO moduleDBO : moduleList) {
            ModuleNumber currentModuleNumber = moduleDBO.getModuleNumber();
            if ((currentModuleNumber.getModuleNumberWithoutVersionInfo().equals(moduleNumber.getValue()))
                    && (currentModuleNumber.getVersion() > maxVersion)) {
                maxVersion = currentModuleNumber.getVersion();
            }
        }

        return moduleNumber.newVersion(maxVersion + 1);

    }

    public Object[] toArray() {
        return moduleList.toArray(new GSDModuleDBO[0]);
    }

    public int getNumberOfEntries() {
        return moduleList.size();
    }

    public void add(GSDModuleDBO versionedGSDModuleDBO) {
        this.moduleList.add(versionedGSDModuleDBO);
        modules.put(versionedGSDModuleDBO.getModuleNumber(), versionedGSDModuleDBO);
    }

    public void createMissingModules(GSDFileDBO gsdFile) throws PersistenceException {
        Map<Integer, GsdModuleModel2> gsdModules = gsdFile.getParsedGsdFileModel().getModuleMap();
        for (Entry<Integer, GsdModuleModel2> entry : gsdModules.entrySet()) {
            ModuleNumber moduleNumber = ModuleNumber.moduleNumber(entry.getValue().getModuleNumber()).get();
            if (!modules.containsKey(moduleNumber)) {
                GSDModuleDBO newGSDModuleDBO = new GSDModuleDBO();
                newGSDModuleDBO.setCreatedBy("Roger");
                newGSDModuleDBO.setCreatedOn(new Date());
                newGSDModuleDBO.setGSDFile(gsdFile);
                newGSDModuleDBO.setModuleId(moduleNumber.getValue());
                newGSDModuleDBO.setName(entry.getValue().getName());
                newGSDModuleDBO = Repository.saveOrUpdate(newGSDModuleDBO);
                moduleList.add(newGSDModuleDBO);
                System.out.println("Creating");
            }
        }
        Repository.refresh(gsdFile);        
    }

}
