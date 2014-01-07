package org.csstudio.config.ioconfig.model.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IGsdModuleModel2Query;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

public class ParsedModuleInfo {

    private final Map<Integer, IGsdModuleModel2Query> moduleInfo;

    private final List<ModuleInfo> infos;

    public ParsedModuleInfo(final Map<Integer, IGsdModuleModel2Query> moduleInfo) {

        Preconditions.checkNotNull(moduleInfo, "moduleInfo must not be null");
        Preconditions.checkArgument(moduleInfo.size() > 0, "size must be > 0");

        this.moduleInfo = new HashMap<Integer, IGsdModuleModel2Query>(moduleInfo);

        infos = new ArrayList<ModuleInfo>();
        for (Entry<Integer, IGsdModuleModel2Query> gsdModuleModel2 : moduleInfo.entrySet()) {
            infos.add(new ModuleInfo(gsdModuleModel2.getValue()));
        }

    }

    public List<ModuleInfo> getModuleInfo() {
        return ImmutableList.copyOf(infos);
    }

    public ModuleInfo getModuleInfo(final ModuleNumber moduleNumber) {

        Preconditions.checkNotNull(moduleNumber, "moduleNumber must not be null");

        if (!moduleInfo.containsKey(moduleNumber.getModuleNumberWithoutVersionInfo())) {
            throw new IllegalStateException("no module for number " + moduleNumber.toString());
        }

        return new ModuleInfo(moduleInfo.get(moduleNumber.getModuleNumberWithoutVersionInfo()));
    }

}