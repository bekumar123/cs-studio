package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ModuleNumber implements Comparable<ModuleNumber> {

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

    public static Optional<ModuleNumber> moduleNumber(Integer value, String test) {
        if ((value == null) || (value < 0)) {
            return Optional.absent();
        } else {
            return Optional.of(new ModuleNumber(value));
        }
    }

    public static Optional<ModuleNumber> moduleNumberAbsent() {
        return Optional.absent();
    }
    
    public Integer getValue() {
        return value;
    }

    public Integer getModuleNumberWithoutVersionInfo() {
        if (value > 10000) {
            return value - 10000;
        } else {
            return value;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleNumber other = (ModuleNumber) obj;

        return Objects.equal(this.value, other.value); 
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    private Double asDouble() {
        if (value > 10000) {
            return getModuleNumberWithoutVersionInfo() + 0.1;
        } else {
            return value * 1.0;
        }
    }
    
    public int compareTo(ModuleNumber moduleNumber) {
        Double n1 = this.asDouble();
        if (moduleNumber != null) {
            return n1.compareTo(moduleNumber.asDouble());
        }
        return 0;
    }

}
