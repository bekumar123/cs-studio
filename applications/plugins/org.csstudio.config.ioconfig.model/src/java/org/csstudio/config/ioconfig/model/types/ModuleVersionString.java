package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Objects;

public class ModuleVersionString {

    private final String value;

    public ModuleVersionString(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ModuleVersionString other = (ModuleVersionString) obj;

        return Objects.equal(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

}
