package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;

public class ModuleId {
    private final Integer value;

    public ModuleId(Integer value) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(value >= 0, "value msut be >= 0");
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }


}
