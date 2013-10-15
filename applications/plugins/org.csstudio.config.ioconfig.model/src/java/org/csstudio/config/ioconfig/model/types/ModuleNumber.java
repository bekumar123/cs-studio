package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ModuleNumber {

    private final Integer value;

    private ModuleNumber(Integer value) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(value >= 0, "value msut be >= 0");
        this.value = value;
    }

    public static Optional<ModuleNumber> moduleNumber(Integer value) {
        if ((value == null) || (value < 0)) {
           return Optional.absent(); 
        } else {
            return Optional.of(new ModuleNumber(value));
        }
    }
    
    public Integer getValue() {
        return value;
    }
        
}
