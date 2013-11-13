package org.csstudio.config.ioconfig.model.types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

public class ModuleList {

    private final Map<ModuleNumber, GSDModuleDBO> modules;
    private final List<GSDModuleDBO> moduleList;
    
    public ModuleList(List<GSDModuleDBO> moduleList) {
        this.moduleList = ImmutableList.copyOf(moduleList);
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

    public Object[] toArray() {
        return moduleList.toArray(new GSDModuleDBO[0]);
    }

    public int getNumberOfEntries() {
        return moduleList.size();
    }

}
