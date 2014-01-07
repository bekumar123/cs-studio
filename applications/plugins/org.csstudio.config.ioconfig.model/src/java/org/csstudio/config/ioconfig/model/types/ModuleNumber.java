package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ModuleNumber implements Comparable<ModuleNumber> {

    private final static int GAP = 1000000;

    private final Integer value;

    public static Optional<ModuleNumber> moduleNumber(Integer value) {
        if ((value == null) || (value < 0)) {
            return Optional.absent();
        } else {
            return Optional.of(new ModuleNumber(value));
        }
    }

    public static Optional<ModuleNumber> moduleNumberAbsent() {
        return Optional.absent();
    }

    private ModuleNumber(Integer value) {
        Preconditions.checkNotNull(value, "value must not be null");
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getModuleNumberWithoutVersionInfo() {
        if (value > GAP) {
            int factor = value / GAP;
            return value - (factor * GAP);
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

    public int compareTo(ModuleNumber moduleNumber) {
        Double n1 = this.asDouble();
        if (moduleNumber != null) {
            return n1.compareTo(moduleNumber.asDouble());
        }
        return 0;
    }

    public boolean isVersioned() {
        return getVersion() > 0;
    }

    public boolean hasHigherVersionThan(ModuleNumber moduleNumber) {
        return getVersion() > moduleNumber.getVersion();
    }

    // Encode the version info inside the module-number.
    // An unversioned module-number has version 0
    // the first versioned module-number has version 1.
    // Example: module-number 1 => first version: 1000001
    public ModuleNumber createNextVersion() {
        return new ModuleNumber(getModuleNumberWithoutVersionInfo() + (GAP * (getVersion() + 1)));
    }

    // Needed for sorting.
    private Double asDouble() {
        return (getModuleNumberWithoutVersionInfo() * 1.0) + (0.001 * getVersion());
    }

    private Integer getVersion() {
        if (value < GAP) {
            return 0;
        } else {
            return value / GAP;
        }
    }

}
