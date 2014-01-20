package org.csstudio.config.ioconfig.model.types;

import java.math.BigDecimal;

import com.google.common.base.Preconditions;

/*
 * Value Object for the ddb_gsd_module.id .
 */
public class ModuleId {
    private final Integer value;

    public ModuleId(BigDecimal value) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(value.intValue() >= 0, "value must be >= 0");
        this.value = value.intValue();
    }

    public ModuleId(Integer value) {
        Preconditions.checkNotNull(value, "value must not be null");
        Preconditions.checkArgument(value >= 0, "value mus be >= 0");
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
