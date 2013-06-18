package org.csstudio.config.ioconfigurator.ldap;

import javax.naming.ldap.LdapName;

import com.google.common.base.Optional;

public class LdapNode {

    private final LdapName name;

    public LdapNode(LdapName name) {
        this.name = name;
    }

    public boolean allowsAddNewNode() {
        return name.toString().startsWith("ou");
    }

    public boolean allowsRenameNode() {
        return !name.toString().startsWith("ecom");
    }

    public Optional<String> getChildAttribute() {
        String nodeName = name.toString();
        if (nodeName.startsWith("efan")) {
            return Optional.of("ecom");
        } else if (nodeName.startsWith("ou")) {
            return Optional.of("efan");
        } else {
            return Optional.absent();
        }
    }

    public String getChildAttributeValue() {
        if (getChildAttribute().get().startsWith("ecom")) {
            return "epicsComponent";
        } else {
            return "epicsFacility";
        }
    }

}
