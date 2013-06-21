package org.csstudio.config.ioconfigurator.ldap;

import javax.naming.ldap.LdapName;

import com.google.common.base.Optional;

public class LdapNode {

    private final LdapName name;

    public LdapNode(LdapName name) {
        this.name = name;
    }

    public boolean isEpicsControll() {
        return name.toString().startsWith("ou");
    }

    public boolean isFacility() {
        return name.toString().startsWith("efan");
    }

    public boolean isEpicsIOC() {
        return name.toString().startsWith("ecom");
    }

    public boolean isLeaf() {
        return name.toString().startsWith("econ");
    }

    public boolean needsCopyOnRename() {
        return name.toString().startsWith("efan");
    }

    public boolean allowsRemovalOfChilds() {
        return isLeaf();
    }

    public boolean isEcon() {
        return getChildAttributeValue().equals("epicsController");
    }
    
    public Optional<String> getChildAttribute() {
        String nodeName = name.toString();
        if (nodeName.startsWith("efan")) {
            return Optional.of("ecom");
        } else if (nodeName.startsWith("ou")) {
            return Optional.of("efan");
        } else if (nodeName.startsWith("ecom")) {
            return Optional.of("econ");
        } else {
            return Optional.absent();
        }
    }

    public String getChildAttributeValue() {
        if (getChildAttribute().get().startsWith("ecom")) {
            return "epicsComponent";
        } else if (getChildAttribute().get().startsWith("econ")) {
            return "epicsController";
        } else {
            return "epicsFacility";
        }
    }

}
