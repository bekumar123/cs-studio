package org.csstudio.config.ioconfig.model.types;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/*
 * Value object for the field version_tag in ddb_gsd_module.
 */
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
