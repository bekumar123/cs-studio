package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;

public class ModuleName {

    private final String value;

    public ModuleName(final String value) {
        
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(!value.isEmpty());
        
        this.value = value;
    }

    public String getValue() {
        return value;
    }
        
}
