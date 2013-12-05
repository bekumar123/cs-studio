package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class VersionTag {

    private String value;

    public VersionTag(String value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(value));
        this.value = value;
    }

    public String getValue() {
        return value;
    }
      
}
