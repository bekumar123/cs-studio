package org.csstudio.config.ioconfig.model.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBOReadOnly;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/*
 * List abstraction for a list of Prototypes.
 */
public class PrototypeList {

    private final Map<ModuleNumber, GSDModuleDBOReadOnly> modules;
    private final List<GSDModuleDBOReadOnly> moduleList;
    private final ConfiguredModuleList configuredModules;
    private Optional<ModuleVersionInfo> editedModuleVersionInfo;

    public PrototypeList(final List<GSDModuleDBOReadOnly> moduleList, final ConfiguredModuleList configuredModules) {

        Preconditions.checkNotNull(moduleList, "moduleList must not be null");
        Preconditions.checkArgument(!moduleList.isEmpty(), "moduleList must not be empty");
        Preconditions.checkNotNull(configuredModules, "configuredModules must not be null");

        this.moduleList = new ArrayList<GSDModuleDBOReadOnly>(moduleList);
        this.configuredModules = configuredModules;
        modules = new LinkedHashMap<ModuleNumber, GSDModuleDBOReadOnly>();
        for (GSDModuleDBOReadOnly gsdModuleDBO : moduleList) {
            modules.put(gsdModuleDBO.getModuleNumber(), gsdModuleDBO);
        }

        editedModuleVersionInfo = Optional.absent();

    }

    public ModuleNumber getFirstModuleNumber() {
        return moduleList.get(0).getModuleNumber();
    }

    public void setEditedModuleVersionInfo(final ModuleVersionInfo editedModuleVersionInfo) {

        Preconditions.checkNotNull(editedModuleVersionInfo, "editedModuleVersionInfo must not be null");
        Preconditions.checkArgument(editedModuleVersionInfo.getModuleNumber().isVersioned(),
                "modulNumber must be versioned");

        this.editedModuleVersionInfo = Optional.of(editedModuleVersionInfo);
    }

    public void unsetEditedModuleVersionInfo() {
        this.editedModuleVersionInfo = Optional.absent();
    }

    public ModuleLabel getModuleLabel(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        //@formatter:off
        if ((editedModuleVersionInfo.isPresent()) && 
            (editedModuleVersionInfo.get().getModuleNumber().equals(moduleNumber))) {
            //@formatter:on
            return editedModuleVersionInfo.get().getModuleLabel(getModuleName(moduleNumber));
        } else {
            return getModule(moduleNumber).getModuleLabel();
        }
    }

    public ModuleName getModuleName(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        if (!modules.containsKey(moduleNumber)) {
            throw new IllegalStateException("No module for number " + moduleNumber);
        }
        return new ModuleName(modules.get(moduleNumber).getName());
    }

    public GSDModuleDBOReadOnly getModule(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        if (!modules.containsKey(moduleNumber)) {
            throw new IllegalStateException("No module for number " + moduleNumber);
        }

        return modules.get(moduleNumber);
    }

    public boolean hasChannelConfiguration(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        Optional<ModuleId> moduleId = moduleId(moduleNumber);
        if (!moduleId.isPresent()) {
            throw new IllegalStateException("can't get id for module: " + moduleNumber);
        }
        return configuredModules.isPresent(moduleId.get());
    }

    private Optional<ModuleId> moduleId(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        for (GSDModuleDBOReadOnly moduleDBO : moduleList) {
            if (moduleDBO.getModuleNumber().equals(moduleNumber)) {
                return Optional.of(new ModuleId(moduleDBO.getId()));
            }
        }
        return Optional.absent();
    }

    public ModuleNumber createNextVersion(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");
        Preconditions.checkArgument(!moduleNumber.isVersioned(), "moduleNumber must not be versioned");

        ModuleNumber maxModuleNumber = maxModuleNumber(moduleNumber);
        ModuleNumber nextVersionedModuleNumber = maxModuleNumber.createNextVersion();

        Preconditions.checkState(nextVersionedModuleNumber.isVersioned(), "must be versioned");

        return nextVersionedModuleNumber;
    }

    private ModuleNumber maxModuleNumber(final ModuleNumber moduleNumber) {
        
        Preconditions.checkArgument(!moduleNumber.isVersioned(), "moduleNumber must not be versioned");
        
        ModuleNumber maxModuleNumber = moduleNumber;
        for (GSDModuleDBOReadOnly moduleDBO : moduleList) {
            ModuleNumber currentModuleNumber = moduleDBO.getModuleNumber();
            if ((currentModuleNumber.getModuleNumberWithoutVersionInfo().equals(moduleNumber.getValue()))
                    && (currentModuleNumber.hasHigherVersionThan(maxModuleNumber))) {
                maxModuleNumber = currentModuleNumber;
            }
        }
        return maxModuleNumber;
    }   
    
    public Object[] toArray() {        
        return moduleList.toArray(new GSDModuleDBOReadOnly[0]);
    }

    public void add(final GSDModuleDBO newGSDModuleDBO) {

        Preconditions.checkNotNull(newGSDModuleDBO, "newGSDModuleDBO must not be null");

        int insertIndex = calculateInsertIndex(newGSDModuleDBO);
        this.moduleList.add(insertIndex, newGSDModuleDBO);
        modules.put(newGSDModuleDBO.getModuleNumber(), newGSDModuleDBO);
    }

    private int calculateInsertIndex(final GSDModuleDBO newGSDModuleDBO) {
        int insertIndex = 0;
        for (GSDModuleDBOReadOnly gsdModuleDBO : moduleList) {
            if (gsdModuleDBO.getModuleNumber().compareTo(newGSDModuleDBO.getModuleNumber()) > 0) {
                break;
            }
            insertIndex++;
        }
        return insertIndex;
    }

    private static Comparator<GSDModuleDBOReadOnly> createComparator() {
        return new Comparator<GSDModuleDBOReadOnly>() {
            @Override
            public int compare(GSDModuleDBOReadOnly o1, GSDModuleDBOReadOnly o2) {

                Optional<ModuleNumber> m1 = Optional.of(o1.getModuleNumber());
                Optional<ModuleNumber> m2 = Optional.of(o2.getModuleNumber());

                if (m1.isPresent() && m2.isPresent()) {
                    return m1.get().compareTo(m2.get());
                }
                return 0;
            }
        };
    };

    public void sort() {
        Collections.sort(moduleList, PrototypeList.createComparator());
    }

    public List<GSDModuleDBO> createMissingModules(final ParsedModuleInfo parsedModuleInfo, final GSDFileDBO gsdFile) {

        Preconditions.checkNotNull(parsedModuleInfo, "parsedModuleInfo must not be null");
        Preconditions.checkNotNull(gsdFile, "gsdFile must not be null");

        List<GSDModuleDBO> missingModules = new ArrayList<GSDModuleDBO>();

        for (ModuleInfo info : parsedModuleInfo.getModuleInfo()) {
            if (!modules.containsKey(info.getModuleNumber())) {
                Preconditions.checkState(!info.getModuleNumber().isVersioned(), "moduleNumber must not be versioned");
                missingModules.add(new GSDModuleDBO(info, gsdFile));
            }
        }

        Collections.sort(missingModules, PrototypeList.createComparator());

        return ImmutableList.copyOf(missingModules);

    }

}
